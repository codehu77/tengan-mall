package com.tengan.mall.payment.application.linepay;

public record ConfirmLinePayPaymentCommand(String orderSn, Long memberId, String transactionId) {
}
