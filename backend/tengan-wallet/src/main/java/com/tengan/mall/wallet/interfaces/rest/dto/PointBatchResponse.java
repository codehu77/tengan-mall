package com.tengan.mall.wallet.interfaces.rest.dto;

import java.time.Instant;

public record PointBatchResponse(Long batchId, int points, Instant earnedAt, Instant expiresAt,
        String sourceOrderSn) {
}
