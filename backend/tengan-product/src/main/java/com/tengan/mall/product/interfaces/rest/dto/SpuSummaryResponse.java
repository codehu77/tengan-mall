package com.tengan.mall.product.interfaces.rest.dto;

public record SpuSummaryResponse(Long id, Long categoryId, Long brandId, String name, String mainImage, int status,
        int skuCount) {
}
