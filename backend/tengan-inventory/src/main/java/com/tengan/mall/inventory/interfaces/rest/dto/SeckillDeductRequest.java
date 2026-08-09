package com.tengan.mall.inventory.interfaces.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SeckillDeductRequest(@NotNull Long skuId, @Min(1) int count) {
}
