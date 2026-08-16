package com.tengan.mall.payment.application.admin;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentRecordView(Long id, String orderSn, Long memberId, String method, BigDecimal amount,
        int status, String gatewayTradeNo, Instant paidAt, Instant createdAt) {
}
