package com.tengan.mall.admin.application.port;

public record SaleAttrItem(Long id, Long categoryId, String name, String unit, boolean searchable, int sort) {
}
