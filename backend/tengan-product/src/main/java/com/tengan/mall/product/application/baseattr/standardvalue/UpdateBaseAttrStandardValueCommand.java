package com.tengan.mall.product.application.baseattr.standardvalue;

public record UpdateBaseAttrStandardValueCommand(String operator, Long id, String label, boolean enabled, int sort) {
}
