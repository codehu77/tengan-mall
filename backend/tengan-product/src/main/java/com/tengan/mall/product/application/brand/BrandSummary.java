package com.tengan.mall.product.application.brand;

public record BrandSummary(Long id, String name, String logo, String descript, String firstLetter, int sort,
        int status) {
}
