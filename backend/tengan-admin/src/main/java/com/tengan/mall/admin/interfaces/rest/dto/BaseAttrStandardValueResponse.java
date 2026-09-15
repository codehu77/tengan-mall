package com.tengan.mall.admin.interfaces.rest.dto;

public record BaseAttrStandardValueResponse(Long id, Long attrId, String label, boolean enabled, int sort) {
}
