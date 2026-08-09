package com.tengan.mall.admin.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TemplateItemResponse(Long id, String name, BigDecimal thresholdAmount, BigDecimal discountAmount,
        int totalCount, int issuedCount, Instant effectiveStart, Instant effectiveEnd, int status) {
}
