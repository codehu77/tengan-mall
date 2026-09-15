package com.tengan.mall.product.interfaces.rest.dto;

public record SaleAttrResponse(Long id, Long categoryId, String name, String unit, boolean searchable, int sort) {
}
