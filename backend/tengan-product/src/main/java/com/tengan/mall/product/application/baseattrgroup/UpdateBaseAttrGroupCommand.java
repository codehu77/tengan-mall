package com.tengan.mall.product.application.baseattrgroup;

public record UpdateBaseAttrGroupCommand(String operator, Long id, String name, int sort) {
}
