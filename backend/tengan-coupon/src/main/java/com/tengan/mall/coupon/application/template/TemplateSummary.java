package com.tengan.mall.coupon.application.template;

import java.math.BigDecimal;
import java.time.Instant;

public record TemplateSummary(Long id, String name, BigDecimal thresholdAmount, BigDecimal discountAmount,
        int totalCount, int issuedCount, Instant effectiveStart, Instant effectiveEnd, int status) {
}
