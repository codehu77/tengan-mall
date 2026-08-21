package com.tengan.mall.seckill.application.activity;

import java.math.BigDecimal;
import java.time.Instant;

public record SkuView(Long id, Long skuId, BigDecimal seckillPrice, int seckillCount, int limitPerUser,
        int soldCount, Instant settledAt) {
}
