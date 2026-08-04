package com.tengan.mall.product.application.baseattr;

public record UpdateBaseAttrCommand(String operator, Long id, Long attrGroupId, String name, boolean searchable,
        int sort) {
}
