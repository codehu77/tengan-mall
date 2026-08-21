package com.tengan.mall.seckill.interfaces.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReserveRequest(@NotNull Long skuId, @NotNull Long memberId, @Min(1) int count) {
}
