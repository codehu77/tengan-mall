package com.tengan.mall.admin.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentRecordResponse(Long id, String orderSn, Long memberId, String method, BigDecimal amount,
        int status, String gatewayTradeNo, Instant paidAt, Instant createdAt) {
}
