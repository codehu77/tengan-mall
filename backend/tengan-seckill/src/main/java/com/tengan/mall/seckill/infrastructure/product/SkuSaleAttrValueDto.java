package com.tengan.mall.seckill.infrastructure.product;

/** tengan-product /internal/products/skus 回應裡 saleAttrValues 的子集，只取 attrValue 拼組規格標籤用。 */
public record SkuSaleAttrValueDto(Long attrId, String attrName, String attrValue) {
}
