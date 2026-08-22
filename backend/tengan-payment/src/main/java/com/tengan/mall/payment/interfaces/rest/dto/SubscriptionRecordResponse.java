package com.tengan.mall.payment.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SubscriptionRecordResponse(Long id, Long memberId, String targetTier, int status,
        String ecpayMerchantTradeNo, BigDecimal periodAmount, int consecutiveFailures, Instant paidUntil,
        Instant benefitExpiredAt, Instant createdAt, Instant cancelledAt) {
}
