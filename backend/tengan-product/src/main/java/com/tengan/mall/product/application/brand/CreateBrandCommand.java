package com.tengan.mall.product.application.brand;

public record CreateBrandCommand(String operator, String name, String logo, String descript, String firstLetter,
        int sort) {
}
