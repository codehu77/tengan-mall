package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummaryItem(Long id, String orderSn, Long memberId, int status, BigDecimal payAmount,
        String paymentMethod, Instant createdAt) {
}
