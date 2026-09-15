package com.tengan.mall.product.application.saleattr.standardvalue;

public record CreateSaleAttrStandardValueCommand(String operator, Long attrId, String label, int sort) {
}
