package com.tengan.mall.payment.application.admin;

public record ListSubscriptionsQuery(Long memberId, Integer status, int page, int pageSize) {
}
