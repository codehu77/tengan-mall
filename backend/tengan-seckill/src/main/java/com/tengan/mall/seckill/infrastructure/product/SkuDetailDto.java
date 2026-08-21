package com.tengan.mall.seckill.infrastructure.product;

import java.math.BigDecimal;

/** tengan-product /internal/products/skus 回應的子集——只取展示端點用得到的欄位，其餘（images/saleAttrValues/...）忽略。 */
public record SkuDetailDto(Long id, Long spuId, String name, BigDecimal price, String mainImage) {
}
