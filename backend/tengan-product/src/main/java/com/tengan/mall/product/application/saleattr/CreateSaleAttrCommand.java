package com.tengan.mall.product.application.saleattr;

public record CreateSaleAttrCommand(String operator, Long categoryId, String name, boolean searchable, int sort) {
}
