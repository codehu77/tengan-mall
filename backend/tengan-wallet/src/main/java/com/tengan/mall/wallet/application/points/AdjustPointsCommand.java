package com.tengan.mall.wallet.application.points;

public record AdjustPointsCommand(Long memberId, int points, String reason, String operator) {
}
