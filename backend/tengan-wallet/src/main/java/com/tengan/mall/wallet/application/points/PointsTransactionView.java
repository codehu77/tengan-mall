package com.tengan.mall.wallet.application.points;

import java.time.Instant;

public record PointsTransactionView(Long id, String type, String status, int points, Integer balanceAfter,
        String title, String description, String orderSn, String channel, String operator, Instant createdAt,
        Instant expiresAt) {
}
