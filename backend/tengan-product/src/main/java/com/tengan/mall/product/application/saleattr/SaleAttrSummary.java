package com.tengan.mall.product.application.saleattr;

public record SaleAttrSummary(Long id, Long categoryId, String name, boolean searchable, int sort) {
}
