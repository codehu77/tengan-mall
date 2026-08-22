package com.tengan.mall.admin.application.seckill;

import com.tengan.mall.admin.application.port.InventoryStockPort;
import com.tengan.mall.admin.application.port.ProductSkuPort;
import com.tengan.mall.admin.application.port.ProductSpuPort;
import com.tengan.mall.admin.application.port.SeckillActivityPort;
import com.tengan.mall.admin.application.port.SeckillActivitySpuSkusResult;
import com.tengan.mall.admin.application.port.SeckillSkuItem;
import com.tengan.mall.admin.application.port.SeckillSpuSkuSuggestion;
import com.tengan.mall.admin.application.port.SkuItem;
import com.tengan.mall.admin.application.port.SkuSaleAttrValueItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 重新編輯既有活動的商品時用：一場活動可以綁多個商品（SPU，見「秒殺改成綁 SPU」規劃文件的延伸決策），
 * 從已存的 skuId 反查回它們各自所屬的 spuId（{@link ProductSkuPort} 本來就有 spuId 欄位，不用新開
 * 端點），按 spuId 分組後各自組出「跟 {@link SuggestSeckillSpuSkusService} 一樣的規格清單」，但
 * 每一列的配額是**目前真的存了多少**，不是重新按比例算的建議值——這樣後台再次打開「設定商品」對話框
 * 才會看到上次存的結果（含多個商品），不會每次都從空白/重新建議開始。
 */
@Service
public class GetSeckillActivitySpuSkusService implements GetSeckillActivitySpuSkusUseCase {

    private final SeckillActivityPort seckillActivityPort;
    private final ProductSkuPort productSkuPort;
    private final ProductSpuPort productSpuPort;
    private final InventoryStockPort inventoryStockPort;

    public GetSeckillActivitySpuSkusService(SeckillActivityPort seckillActivityPort, ProductSkuPort productSkuPort,
            ProductSpuPort productSpuPort, InventoryStockPort inventoryStockPort) {
        this.seckillActivityPort = seckillActivityPort;
        this.productSkuPort = productSkuPort;
        this.productSpuPort = productSpuPort;
        this.inventoryStockPort = inventoryStockPort;
    }

    @Override
    public List<SeckillActivitySpuSkusResult> get(Long activityId) {
        var detail = seckillActivityPort.getActivity(activityId);
        if (detail.skus().isEmpty()) {
            return List.of();
        }
        Map<Long, SeckillSkuItem> existingBySkuId = detail.skus().stream()
                .collect(Collectors.toMap(SeckillSkuItem::skuId, Function.identity()));

        List<Long> allSkuIds = detail.skus().stream().map(SeckillSkuItem::skuId).toList();
        List<SkuItem> allSkuInfo = productSkuPort.batchGet(allSkuIds); // 商品已在 tengan-product 被刪除的 skuId 會被安全略過
        Map<Long, List<SkuItem>> skuInfoBySpuId = allSkuInfo.stream()
                .collect(Collectors.groupingBy(SkuItem::spuId));

        List<SeckillActivitySpuSkusResult> results = new ArrayList<>();
        for (var entry : skuInfoBySpuId.entrySet()) {
            Long spuId = entry.getKey();
            var spu = productSpuPort.getSpu(spuId);

            List<SeckillSpuSkuSuggestion> items = spu.skus().stream()
                    .map(sku -> {
                        SeckillSkuItem existing = existingBySkuId.get(sku.id());
                        int quota = existing != null ? existing.seckillCount() : 0;
                        return new SeckillSpuSkuSuggestion(sku.id(), variantLabel(sku), sku.mainImage(),
                                realStock(sku.id()), quota);
                    })
                    .toList();

            // 這個 SPU 底下實際有存過配額的任一列，拿它的 price/limit 當這個商品區塊的代表值（前端逐列還是可以再各自覆蓋）。
            SeckillSkuItem representative = entry.getValue().stream()
                    .map(sku -> existingBySkuId.get(sku.id()))
                    .filter(java.util.Objects::nonNull)
                    .findFirst()
                    .orElse(null);
            if (representative == null) {
                continue;
            }
            results.add(new SeckillActivitySpuSkusResult(spuId, spu.name(), spu.mainImage(),
                    representative.seckillPrice(), representative.limitPerUser(), items));
        }
        return results;
    }

    private int realStock(Long skuId) {
        return inventoryStockPort.listSkus(null, skuId, 1, 100).items().stream()
                .mapToInt(item -> Math.max(0, item.stock() - item.lockedStock()))
                .sum();
    }

    private String variantLabel(SkuItem sku) {
        if (sku.saleAttrValues() == null || sku.saleAttrValues().isEmpty()) {
            return sku.name();
        }
        return sku.saleAttrValues().stream().map(SkuSaleAttrValueItem::attrValue).reduce((a, b) -> a + "/" + b)
                .orElse(sku.name());
    }
}
