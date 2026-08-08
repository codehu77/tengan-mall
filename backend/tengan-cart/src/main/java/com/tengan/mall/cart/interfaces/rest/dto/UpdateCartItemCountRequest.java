package com.tengan.mall.cart.interfaces.rest.dto;

import jakarta.validation.constraints.Min;

public record UpdateCartItemCountRequest(@Min(1) int count) {
}
