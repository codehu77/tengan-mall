package com.tengan.mall.cart.interfaces.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(@NotNull Long skuId, @Min(1) int count, String specText) {
}
