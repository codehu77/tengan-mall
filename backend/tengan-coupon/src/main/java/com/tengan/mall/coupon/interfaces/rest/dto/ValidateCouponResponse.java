package com.tengan.mall.coupon.interfaces.rest.dto;

import java.math.BigDecimal;

public record ValidateCouponResponse(boolean valid, BigDecimal discountAmount) {
}
