package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.model.SpuStatus;
import com.tengan.mall.product.domain.repository.OrderSaleCountTaskRepository;
import com.tengan.mall.product.domain.repository.SkuSaleCountRepository;
import com.tengan.mall.product.domain.repository.SpuRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * order.completed 消費端邏輯：先搶冪等操作權（sku_sale_count_task），成功才對每個 sku 直接 UPDATE
 * sale_count（繞過 Spu 聚合根，見 SkuSaleCountRepository 說明），再對受影響的每個 SPU 重新組一次
 * 搜尋文件、重發 product.upserted，讓 tengan-search 既有的 ProductEventListener.onUpserted 直接
 * 全量覆蓋——ES 端零新增程式碼，跟 UpdateSpuService/PublishSpuService 共用同一套組裝邏輯
 * （SpuSearchDocumentAssembler 是 package-private，這也是這個類別必須放在 application.spu 的原因）。
 */
@Service
public class RecordOrderCompletedSaleCountService implements RecordOrderCompletedSaleCountUseCase {

    private final OrderSaleCountTaskRepository taskRepository;
    private final SkuSaleCountRepository skuSaleCountRepository;
    private final SpuRepository spuRepository;
    private final SpuSearchDocumentAssembler searchDocumentAssembler;
    private final ProductSearchEventPublisherPort searchEventPublisher;

    public RecordOrderCompletedSaleCountService(OrderSaleCountTaskRepository taskRepository,
            SkuSaleCountRepository skuSaleCountRepository, SpuRepository spuRepository,
            SpuSearchDocumentAssembler searchDocumentAssembler, ProductSearchEventPublisherPort searchEventPublisher) {
        this.taskRepository = taskRepository;
        this.skuSaleCountRepository = skuSaleCountRepository;
        this.spuRepository = spuRepository;
        this.searchDocumentAssembler = searchDocumentAssembler;
        this.searchEventPublisher = searchEventPublisher;
    }

    @Override
    @Transactional
    public void record(String orderSn, List<SkuCountItem> items) {
        if (!taskRepository.tryClaim(orderSn)) {
            return; // 重複投遞，冪等 no-op
        }
        Set<Long> affectedSpuIds = new LinkedHashSet<>();
        for (SkuCountItem item : items) {
            skuSaleCountRepository.increment(item.skuId(), item.count());
            affectedSpuIds.add(item.spuId());
        }
        // item.spuId() 是 tengan-order 下單當下就存好的快照（sku 所屬 spu 建立後不會變），
        // 直接信任、不用額外查詢反查。
        for (Long spuId : affectedSpuIds) {
            spuRepository.findById(spuId).ifPresent(spu -> {
                // 只有上架中的商品才在 ES 索引裡，跟 UpdateSpuService 的 wasOnShelf 守門邏輯一致——
                // 已下架的 SPU 不該因為這次事件被重新加回索引，但 MySQL 的 sale_count 還是照樣加。
                if (spu.getStatus() == SpuStatus.ON_SHELF) {
                    searchEventPublisher.publishUpserted(searchDocumentAssembler.assemble(spu));
                }
            });
        }
    }
}
