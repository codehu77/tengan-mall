package com.tengan.mall.wallet.interfaces.rest.dto;

import java.time.Instant;

public record PointsTransactionResponse(Long id, String type, String status, int points, Integer balanceAfter,
        String title, String description, String orderSn, String channel, String operator, Instant createdAt,
        Instant expiresAt) {
}
