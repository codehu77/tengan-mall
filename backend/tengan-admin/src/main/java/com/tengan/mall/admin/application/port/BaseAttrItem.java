package com.tengan.mall.admin.application.port;

public record BaseAttrItem(Long id, Long categoryId, Long attrGroupId, String name, boolean searchable, int sort) {
}
