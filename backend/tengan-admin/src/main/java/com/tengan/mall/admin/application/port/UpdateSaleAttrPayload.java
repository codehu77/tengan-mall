package com.tengan.mall.admin.application.port;

public record UpdateSaleAttrPayload(String name, String unit, boolean searchable, int sort) {
}
