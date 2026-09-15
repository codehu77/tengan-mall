package com.tengan.mall.admin.interfaces.rest.dto;

public record SaleAttrResponse(Long id, Long categoryId, String name, String unit, boolean searchable, int sort) {
}
