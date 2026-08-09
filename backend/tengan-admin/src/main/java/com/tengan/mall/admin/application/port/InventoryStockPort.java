package com.tengan.mall.admin.application.port;

/** 呼叫 tengan-inventory 的庫存 internal 端點，跟 {@link ProductBrandPort} 同樣的純代理原則。 */
public interface InventoryStockPort {

    SkuStockPageResult listSkus(Long wareId, Long skuIdKeyword, int page, int pageSize);

    void createStock(CreateStockPayload payload, String operatorToken);

    void adjustStock(Long skuId, AdjustStockPayload payload, String operatorToken);
}
