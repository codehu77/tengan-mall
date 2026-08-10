package com.tengan.mall.order.infrastructure.coupon.dto;

import java.math.BigDecimal;

public record InternalValidateCouponRequestDto(Long userId, Long couponId, BigDecimal amount) {
}
