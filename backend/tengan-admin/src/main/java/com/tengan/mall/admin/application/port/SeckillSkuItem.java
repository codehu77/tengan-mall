package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;
import java.time.Instant;

public record SeckillSkuItem(Long id, Long skuId, BigDecimal seckillPrice, int seckillCount, int limitPerUser,
        int soldCount, Instant settledAt) {
}
