package com.tengan.mall.product.application.baseattr.standardvalue;

public record CreateBaseAttrStandardValueCommand(String operator, Long attrId, String label, int sort) {
}
