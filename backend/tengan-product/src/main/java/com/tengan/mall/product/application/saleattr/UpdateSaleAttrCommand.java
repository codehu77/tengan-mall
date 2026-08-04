package com.tengan.mall.product.application.saleattr;

public record UpdateSaleAttrCommand(String operator, Long id, String name, boolean searchable, int sort) {
}
