package com.tengan.mall.wallet.interfaces.rest.dto;

import java.math.BigDecimal;

public record PointsSummaryResponse(int availablePoints, int pendingPoints, int expiringPoints,
        int expiringWithinDays, BigDecimal pointValueRatio) {
}
