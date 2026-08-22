package com.tengan.mall.admin.application.seckill;

import com.tengan.mall.admin.application.port.InventoryStockPort;
import com.tengan.mall.admin.application.port.ProductSpuPort;
import com.tengan.mall.admin.application.port.SeckillSpuSkuSuggestion;
import com.tengan.mall.admin.application.port.SkuItem;
import com.tengan.mall.admin.application.port.SkuSaleAttrValueItem;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 秒殺改成綁 SPU 的核心：後台只填一個「總量」，這裡幫每個規格（顏色/容量）算出建議配額——
 * 依真實庫存（{@code stock - lockedStock}）比例分配，四捨五入後的誤差補到庫存最多的那個規格，
 * 讓加總精確等於總量。這只是「建議值」，後台可以在畫面上自由覆蓋每一列（包含填 0），送出時另外
 * 驗證加總不超過總量——這裡不做那個驗證，純粹算建議值（見「秒殺改成綁 SPU」規劃文件）。
 */
@Service
public class SuggestSeckillSpuSkusService implements SuggestSeckillSpuSkusUseCase {

    private final ProductSpuPort productSpuPort;
    private final InventoryStockPort inventoryStockPort;

    public SuggestSeckillSpuSkusService(ProductSpuPort productSpuPort, InventoryStockPort inventoryStockPort) {
        this.productSpuPort = productSpuPort;
        this.inventoryStockPort = inventoryStockPort;
    }

    @Override
    public List<SeckillSpuSkuSuggestion> suggest(Long spuId, int totalQuota) {
        List<SkuItem> skus = productSpuPort.getSpu(spuId).skus();

        Map<Long, Integer> stockBySkuId = new LinkedHashMap<>();
        for (SkuItem sku : skus) {
            stockBySkuId.put(sku.id(), realStock(sku.id()));
        }
        Map<Long, Integer> quotaBySkuId = splitProportionally(stockBySkuId, totalQuota);

        return skus.stream()
                .map(sku -> new SeckillSpuSkuSuggestion(sku.id(), variantLabel(sku), sku.mainImage(),
                        stockBySkuId.get(sku.id()), quotaBySkuId.get(sku.id())))
                .toList();
    }

    private int realStock(Long skuId) {
        return inventoryStockPort.listSkus(null, skuId, 1, 100).items().stream()
                .mapToInt(item -> Math.max(0, item.stock() - item.lockedStock()))
                .sum();
    }

    private Map<Long, Integer> splitProportionally(Map<Long, Integer> stockBySkuId, int totalQuota) {
        Map<Long, Integer> quotaBySkuId = new LinkedHashMap<>();
        int totalStock = stockBySkuId.values().stream().mapToInt(Integer::intValue).sum();
        if (totalStock <= 0 || totalQuota <= 0) {
            stockBySkuId.keySet().forEach(skuId -> quotaBySkuId.put(skuId, 0));
            return quotaBySkuId;
        }

        int allocated = 0;
        Long largestStockSkuId = null;
        int largestStock = -1;
        for (var entry : stockBySkuId.entrySet()) {
            int share = (int) Math.round(totalQuota * (entry.getValue() / (double) totalStock));
            share = Math.min(share, entry.getValue());
            quotaBySkuId.put(entry.getKey(), share);
            allocated += share;
            if (entry.getValue() > largestStock) {
                largestStock = entry.getValue();
                largestStockSkuId = entry.getKey();
            }
        }

        int diff = totalQuota - allocated;
        if (diff != 0 && largestStockSkuId != null) {
            int adjusted = quotaBySkuId.get(largestStockSkuId) + diff;
            adjusted = Math.max(0, Math.min(adjusted, stockBySkuId.get(largestStockSkuId)));
            quotaBySkuId.put(largestStockSkuId, adjusted);
        }
        return quotaBySkuId;
    }

    private String variantLabel(SkuItem sku) {
        if (sku.saleAttrValues() == null || sku.saleAttrValues().isEmpty()) {
            return sku.name();
        }
        return sku.saleAttrValues().stream().map(SkuSaleAttrValueItem::attrValue).reduce((a, b) -> a + "/" + b)
                .orElse(sku.name());
    }
}
