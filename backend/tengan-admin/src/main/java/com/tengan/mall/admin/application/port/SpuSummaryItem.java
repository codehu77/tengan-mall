package com.tengan.mall.admin.application.port;

public record SpuSummaryItem(Long id, Long categoryId, Long brandId, String name, String mainImage, int status,
        int skuCount) {
}
