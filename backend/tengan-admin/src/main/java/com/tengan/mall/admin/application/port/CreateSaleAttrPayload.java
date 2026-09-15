package com.tengan.mall.admin.application.port;

public record CreateSaleAttrPayload(Long categoryId, String name, String unit, boolean searchable, int sort) {
}
