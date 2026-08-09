package com.tengan.mall.coupon.application.membercoupon;

import java.math.BigDecimal;

public record ValidateCouponResult(boolean valid, BigDecimal discountAmount) {
}
