package com.tengan.mall.coupon.application.template;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateTemplateCommand(String operator, String name, BigDecimal thresholdAmount,
        BigDecimal discountAmount, int totalCount, Instant effectiveStart, Instant effectiveEnd) {
}
