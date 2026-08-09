package com.tengan.mall.admin.interfaces.rest.dto;

public record TaskItemResponse(Long id, String orderSn, int status, String createdAt) {
}
