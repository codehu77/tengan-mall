package com.tengan.mall.seckill.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SkuResponse(Long id, Long skuId, BigDecimal seckillPrice, int seckillCount, int limitPerUser,
        int soldCount, Instant settledAt) {
}
