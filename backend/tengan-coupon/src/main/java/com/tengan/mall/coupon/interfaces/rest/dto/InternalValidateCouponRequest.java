package com.tengan.mall.coupon.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** 服務對服務版本的 ValidateCouponRequest，userId 由呼叫端（tengan-order）明確帶入，不是從 JWT 解。 */
public record InternalValidateCouponRequest(@NotNull Long userId, @NotNull Long couponId, @NotNull BigDecimal amount) {
}
