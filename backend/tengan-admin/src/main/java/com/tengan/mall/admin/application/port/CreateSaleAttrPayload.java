package com.tengan.mall.admin.application.port;

public record CreateSaleAttrPayload(Long categoryId, String name, boolean searchable, int sort) {
}
