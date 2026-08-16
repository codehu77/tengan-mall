package com.tengan.mall.payment.application.payment;

import java.math.BigDecimal;

public record PaymentStatusView(String orderSn, String method, int status, BigDecimal amount) {

    public static final int STATUS_PENDING = 1;
    public static final int STATUS_PAID = 2;
}
