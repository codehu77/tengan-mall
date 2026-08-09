package com.tengan.mall.coupon.application.membercoupon;

import java.math.BigDecimal;
import java.time.Instant;

public record MyCouponView(Long id, Long templateId, String templateName, BigDecimal thresholdAmount,
        BigDecimal discountAmount, int useStatus, String orderSn, Instant receivedAt) {
}
