package com.tengan.mall.product.application.category;

public record UpdateCategoryCommand(String operator, Long id, String name, String icon, int sort) {
}
