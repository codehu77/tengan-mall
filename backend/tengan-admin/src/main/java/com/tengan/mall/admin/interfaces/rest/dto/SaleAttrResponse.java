package com.tengan.mall.admin.interfaces.rest.dto;

public record SaleAttrResponse(Long id, Long categoryId, String name, boolean searchable, int sort) {
}
