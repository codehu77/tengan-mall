package com.tengan.mall.payment.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SubscriptionPaymentResponse(Long id, String gwsr, boolean success, BigDecimal amount,
        int totalSuccessTimes, Instant processDate, Instant createdAt) {
}
