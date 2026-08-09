package com.tengan.mall.coupon.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

/** consume/revert 共用同一種 request 形狀。 */
public record OrderSnRequest(@NotBlank String orderSn) {
}
