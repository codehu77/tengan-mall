package com.tengan.mall.admin.application.port;

public record UpdateBaseAttrPayload(Long attrGroupId, String name, String unit, boolean searchable, int sort) {
}
