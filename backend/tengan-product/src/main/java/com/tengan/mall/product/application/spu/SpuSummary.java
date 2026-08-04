package com.tengan.mall.product.application.spu;

public record SpuSummary(Long id, Long categoryId, Long brandId, String name, String mainImage, int status,
        int skuCount) {
}
