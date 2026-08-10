package com.tengan.mall.order.application.port;

import java.math.BigDecimal;

/** tengan-product /internal/products/skus 回應的扁平投影，含下單當下要落地快照的欄位。 */
public record PricedSkuInfo(Long skuId, Long spuId, String name, BigDecimal price, String mainImage) {
}
