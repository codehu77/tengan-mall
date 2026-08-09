package com.tengan.mall.coupon.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TemplateResponse(Long id, String name, BigDecimal thresholdAmount, BigDecimal discountAmount,
        int totalCount, int issuedCount, Instant effectiveStart, Instant effectiveEnd, int status) {
}
