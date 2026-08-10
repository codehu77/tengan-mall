package com.tengan.mall.order.application.order;

public record CancelOrderCommand(Long memberId, String orderSn) {
}
