package com.tengan.mall.coupon.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record MyCouponResponse(Long id, Long templateId, String templateName, BigDecimal thresholdAmount,
        BigDecimal discountAmount, int useStatus, String orderSn, Instant receivedAt) {
}
