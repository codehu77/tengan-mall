package com.tengan.mall.admin.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummaryResponse(Long id, String orderSn, Long memberId, int status, BigDecimal payAmount,
        String paymentMethod, Instant createdAt) {
}
