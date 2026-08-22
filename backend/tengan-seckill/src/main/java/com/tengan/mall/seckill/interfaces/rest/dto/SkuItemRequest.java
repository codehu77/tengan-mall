package com.tengan.mall.seckill.interfaces.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** seckillCount 允許 0——代表這個規格不參與/名額用完，跟被搶完歸零是同一種狀態（見 SeckillSku 網域說明）。 */
public record SkuItemRequest(@NotNull Long skuId, @NotNull @DecimalMin("0") BigDecimal seckillPrice,
        @Min(0) int seckillCount, @Min(1) int limitPerUser) {
}
