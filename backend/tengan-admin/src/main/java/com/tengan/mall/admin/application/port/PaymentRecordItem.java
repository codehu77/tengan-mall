package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentRecordItem(Long id, String orderSn, Long memberId, String method, BigDecimal amount,
        int status, String gatewayTradeNo, Instant paidAt, Instant createdAt) {
}
