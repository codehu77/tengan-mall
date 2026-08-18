package com.tengan.mall.payment.interfaces.rest.dto;

import java.time.Instant;

public record MySubscriptionResponse(boolean subscribed, String targetTier, String status, Instant paidUntil,
        boolean autoRenew) {
}
