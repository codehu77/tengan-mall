package com.tengan.mall.product.application.saleattr.standardvalue;

public record UpdateSaleAttrStandardValueCommand(String operator, Long id, String label, boolean enabled, int sort) {
}
