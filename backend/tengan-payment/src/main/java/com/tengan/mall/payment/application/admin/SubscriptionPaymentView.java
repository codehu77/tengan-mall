package com.tengan.mall.payment.application.admin;

import java.math.BigDecimal;
import java.time.Instant;

public record SubscriptionPaymentView(Long id, String gwsr, boolean success, BigDecimal amount,
        int totalSuccessTimes, Instant processDate, Instant createdAt) {
}
