package com.tengan.mall.cart.infrastructure.product.dto;

import java.math.BigDecimal;

/**
 * tengan-product 的 /internal/products/skus 回應（SkuDetailResponse）欄位子集——這裡只取
 * 購物車用得到的欄位（id/spuId/name/price/mainImage），images/saleAttrValues 等其餘欄位
 * 反序列化時直接忽略。
 */
public record SkuDetailDto(Long id, Long spuId, String name, BigDecimal price, String mainImage) {
}
