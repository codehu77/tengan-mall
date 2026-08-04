package com.tengan.mall.product.domain.model;

/** Sku 的銷售屬性實際值（顏色/容量這類，驅動規格切換 UI）。attrName 冗餘存一份是同 DB 內讀效能考量。 */
public record SkuSaleAttrValue(Long attrId, String attrName, String attrValue) {
}
