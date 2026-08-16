package com.tengan.mall.order.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(@NotBlank String orderToken, @NotNull @Valid ReceiverInfoRequest receiverInfo,
        @NotBlank String paymentMethod, Long couponId, Integer pointsUsed, String remark) {
}
