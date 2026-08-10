package com.tengan.mall.order.infrastructure.product.dto;

import java.math.BigDecimal;

/** 只取下單用得到的欄位子集（id/spuId/name/price/mainImage），其餘 images/saleAttrValues 等欄位忽略。 */
public record SkuDetailDto(Long id, Long spuId, String name, BigDecimal price, String mainImage) {
}
