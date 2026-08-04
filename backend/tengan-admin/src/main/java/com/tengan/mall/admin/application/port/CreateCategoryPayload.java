package com.tengan.mall.admin.application.port;

public record CreateCategoryPayload(Long parentId, String name, String icon, int sort) {
}
