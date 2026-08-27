package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-inventory 的庫存 internal 端點，跟 {@link ProductBrandPort} 同樣的純代理原則。 */
public interface InventoryStockPort {

    SkuStockPageResult listSkus(Long wareId, Long skuIdKeyword, boolean onlyLowStock, int page, int pageSize);

    void createStock(CreateStockPayload payload, String operatorToken);

    void adjustStock(Long skuId, AdjustStockPayload payload, String operatorToken);

    int countLowStock();

    /** SPU 列表頁「啟用/重設防超賣保護」點擊當下用，回報這批 skuId 目前各自的可用庫存。 */
    List<SkuStockSummary> sumAvailableStock(List<Long> skuIds);
}
