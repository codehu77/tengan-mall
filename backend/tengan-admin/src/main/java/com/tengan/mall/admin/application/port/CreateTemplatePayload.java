package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateTemplatePayload(String name, BigDecimal thresholdAmount, BigDecimal discountAmount,
        int totalCount, Instant effectiveStart, Instant effectiveEnd) {
}
