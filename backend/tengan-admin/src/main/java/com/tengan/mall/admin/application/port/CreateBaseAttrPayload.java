package com.tengan.mall.admin.application.port;

public record CreateBaseAttrPayload(Long categoryId, Long attrGroupId, String name, boolean searchable, int sort) {
}
