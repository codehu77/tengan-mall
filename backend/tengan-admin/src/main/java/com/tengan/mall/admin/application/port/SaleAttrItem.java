package com.tengan.mall.admin.application.port;

public record SaleAttrItem(Long id, Long categoryId, String name, boolean searchable, int sort) {
}
