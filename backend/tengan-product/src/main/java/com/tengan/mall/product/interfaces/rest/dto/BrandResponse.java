package com.tengan.mall.product.interfaces.rest.dto;

public record BrandResponse(Long id, String name, String logo, String descript, String firstLetter, int sort,
        int status) {
}
