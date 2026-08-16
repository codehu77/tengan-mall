package com.tengan.mall.payment.application.payment;

public record InitiatePaymentCommand(String orderSn, Long memberId, String method) {
}
