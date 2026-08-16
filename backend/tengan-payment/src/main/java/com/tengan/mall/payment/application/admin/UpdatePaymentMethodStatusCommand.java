package com.tengan.mall.payment.application.admin;

public record UpdatePaymentMethodStatusCommand(String operator, String method, boolean enabled) {
}
