package com.tengan.mall.admin.application.port;

public record UpdateBaseAttrPayload(Long attrGroupId, String name, boolean searchable, int sort) {
}
