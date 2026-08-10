package com.tengan.mall.order.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminCancelOrderRequest(@NotBlank String reason) {
}
