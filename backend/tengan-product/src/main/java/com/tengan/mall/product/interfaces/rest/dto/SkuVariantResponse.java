package com.tengan.mall.product.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.List;

public record SkuVariantResponse(Long skuId, String skuName, BigDecimal price, String mainImage, int saleCount,
        List<SearchAttrResponse> saleAttrs) {
}
