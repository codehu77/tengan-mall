package com.tengan.mall.product.application.baseattr;

public record CreateBaseAttrCommand(String operator, Long categoryId, Long attrGroupId, String name,
        boolean searchable, int sort) {
}
