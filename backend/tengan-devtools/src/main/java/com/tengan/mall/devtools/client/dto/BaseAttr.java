package com.tengan.mall.devtools.client.dto;

public record BaseAttr(Long id, Long categoryId, Long attrGroupId, String name, boolean searchable, int sort) {
}
