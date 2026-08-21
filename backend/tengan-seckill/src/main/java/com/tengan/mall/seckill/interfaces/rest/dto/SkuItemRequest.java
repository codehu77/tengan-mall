package com.tengan.mall.seckill.interfaces.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SkuItemRequest(@NotNull Long skuId, @NotNull @DecimalMin("0") BigDecimal seckillPrice,
        @Min(1) int seckillCount, @Min(1) int limitPerUser) {
}
