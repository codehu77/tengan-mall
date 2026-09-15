package com.tengan.mall.product.application.saleattr;

public record CreateSaleAttrCommand(String operator, Long categoryId, String name, String unit, boolean searchable,
        int sort) {
}
