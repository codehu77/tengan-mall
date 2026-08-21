package com.tengan.mall.admin.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SeckillSkuResponse(Long id, Long skuId, BigDecimal seckillPrice, int seckillCount, int limitPerUser,
        int soldCount, Instant settledAt) {
}
