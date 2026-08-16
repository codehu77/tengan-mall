package com.tengan.mall.payment.application.port;

import java.math.BigDecimal;

public record OrderDetailForPayment(String orderSn, Long memberId, int status, BigDecimal payAmount,
        String itemName) {

    public static final int STATUS_PENDING_PAYMENT = 1;
}
