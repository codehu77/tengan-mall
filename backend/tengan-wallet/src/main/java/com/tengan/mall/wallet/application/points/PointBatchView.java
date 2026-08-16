package com.tengan.mall.wallet.application.points;

import java.time.Instant;

public record PointBatchView(Long id, int points, Instant earnedAt, Instant expiresAt, String sourceOrderSn) {
}
