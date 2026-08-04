package com.tengan.mall.admin.interfaces.rest.dto;

public record BaseAttrResponse(Long id, Long categoryId, Long attrGroupId, String name, boolean searchable,
        int sort) {
}
