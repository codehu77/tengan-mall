package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;
import java.time.Instant;

public record TemplateItem(Long id, String name, BigDecimal thresholdAmount, BigDecimal discountAmount,
        int totalCount, int issuedCount, Instant effectiveStart, Instant effectiveEnd, int status) {
}
