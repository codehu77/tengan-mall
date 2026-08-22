package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;
import java.time.Instant;

public record SubscriptionPaymentItem(Long id, String gwsr, boolean success, BigDecimal amount,
        int totalSuccessTimes, Instant processDate, Instant createdAt) {
}
