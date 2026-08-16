package com.tengan.mall.wallet.application.points;

import java.math.BigDecimal;

public record GetPointsSummaryResult(int availablePoints, int pendingPoints, int expiringPoints,
        int expiringWithinDays, BigDecimal pointValueRatio) {
}
