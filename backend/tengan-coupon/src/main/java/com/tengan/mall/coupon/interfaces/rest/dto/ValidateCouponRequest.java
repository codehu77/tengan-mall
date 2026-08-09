package com.tengan.mall.coupon.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ValidateCouponRequest(@NotNull Long couponId, @NotNull BigDecimal amount) {
}
