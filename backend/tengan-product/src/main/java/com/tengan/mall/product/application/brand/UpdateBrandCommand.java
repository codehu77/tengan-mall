package com.tengan.mall.product.application.brand;

public record UpdateBrandCommand(String operator, Long id, String name, String logo, String descript,
        String firstLetter, int sort) {
}
