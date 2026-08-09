package com.tengan.mall.coupon.application.membercoupon;

import java.math.BigDecimal;

public record ValidateCouponCommand(Long userId, Long couponId, BigDecimal amount) {
}
