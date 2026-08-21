package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;

public record SeckillSkuItemPayload(Long skuId, BigDecimal seckillPrice, int seckillCount, int limitPerUser) {
}
