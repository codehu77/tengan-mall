package com.tengan.mall.product.application.category;

public record CreateCategoryCommand(String operator, Long parentId, String name, String icon, int sort) {
}
