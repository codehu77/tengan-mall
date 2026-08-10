package com.tengan.mall.order.application.admin;

public record AdminCancelOrderCommand(String operator, String orderSn, String reason) {
}
