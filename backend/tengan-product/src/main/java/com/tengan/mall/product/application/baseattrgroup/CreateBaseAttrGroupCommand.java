package com.tengan.mall.product.application.baseattrgroup;

public record CreateBaseAttrGroupCommand(String operator, Long categoryId, String name, int sort) {
}
