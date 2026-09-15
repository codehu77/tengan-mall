package com.tengan.mall.admin.application.port;

public record CreateBaseAttrPayload(Long categoryId, Long attrGroupId, String name, String unit, boolean searchable,
        int sort) {
}
