package com.tengan.mall.product.interfaces.rest.dto;

public record SaleAttrResponse(Long id, Long categoryId, String name, boolean searchable, int sort) {
}
