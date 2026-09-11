package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.SpuNotFoundException;
import com.tengan.mall.product.domain.exception.SpuOnShelfException;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.model.SpuStatus;
import com.tengan.mall.product.domain.repository.SpuRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteSpuService implements DeleteSpuUseCase {

    private final SpuRepository spuRepository;
    private final ProductSearchEventPublisherPort searchEventPublisher;
    private final ProductLaunchConfigEventPublisherPort launchConfigEventPublisher;
    private final ProductMediaUsageEventPublisherPort mediaUsageEventPublisher;

    public DeleteSpuService(SpuRepository spuRepository, ProductSearchEventPublisherPort searchEventPublisher,
            ProductLaunchConfigEventPublisherPort launchConfigEventPublisher,
            ProductMediaUsageEventPublisherPort mediaUsageEventPublisher) {
        this.spuRepository = spuRepository;
        this.searchEventPublisher = searchEventPublisher;
        this.launchConfigEventPublisher = launchConfigEventPublisher;
        this.mediaUsageEventPublisher = mediaUsageEventPublisher;
    }

    @Override
    @Transactional
    public void delete(DeleteSpuCommand command) {
        Spu spu = spuRepository.findById(command.spuId())
                .orElseThrow(() -> new SpuNotFoundException(command.spuId()));
        if (spu.getStatus() == SpuStatus.ON_SHELF) {
            throw new SpuOnShelfException(command.spuId());
        }
        spuRepository.deleteById(command.spuId());
        List<Long> skuIds = spu.getSkus().stream().map(sku -> sku.getId()).toList();
        // 防禦性發送：上架中不可刪的守衛已經擋在前面，這裡多半是刪除本來就不在索引裡的 spu，
        // tengan-search 收到刪除一個不存在的文件是冪等操作，不會出錯。
        searchEventPublisher.publishRemoved(spu.getId(), skuIds);
        // 整顆 SPU 直接刪除，跟 UpdateSpuService 整批替換 SKU 是同一種「這些 skuId 從此不存在」
        // 的情境，一定要通知 tengan-inventory 清掉 sku_launch_config 副本列，不然會永久孤兒——
        // 之前這裡漏了這一行，只發了 search 移除事件，沒發 launch-config 移除事件。
        launchConfigEventPublisher.publishRemoved(spu.getId(), skuIds);

        // 傳空 list 宣告「這個 owner 現在什麼都不用」，tengan-media 收到後把原本 CONFIRMED 的圖
        // 全部打回 PENDING 交給 GC 回收（跟 UpdateSpuService 同一套 sync 機制，delete 只是空集合的特例）。
        mediaUsageEventPublisher.publishSynced(spu.getId(), List.of());
    }
}
