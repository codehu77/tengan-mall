package com.tengan.mall.product.application.saleattr;

public record SaleAttrSummary(Long id, Long categoryId, String name, String unit, boolean searchable, int sort) {
}
