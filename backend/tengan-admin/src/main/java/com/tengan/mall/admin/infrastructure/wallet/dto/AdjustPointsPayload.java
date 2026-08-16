package com.tengan.mall.admin.infrastructure.wallet.dto;

public record AdjustPointsPayload(Long memberId, int points, String reason) {
}
