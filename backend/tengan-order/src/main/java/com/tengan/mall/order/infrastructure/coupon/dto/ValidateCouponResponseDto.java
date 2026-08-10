package com.tengan.mall.order.infrastructure.coupon.dto;

import java.math.BigDecimal;

public record ValidateCouponResponseDto(boolean valid, BigDecimal discountAmount) {
}
