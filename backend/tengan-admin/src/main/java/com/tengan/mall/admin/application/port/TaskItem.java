package com.tengan.mall.admin.application.port;

public record TaskItem(Long id, String orderSn, int status, String createdAt) {
}
