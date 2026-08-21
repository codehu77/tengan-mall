package com.tengan.mall.seckill.application.activity;

import java.math.BigDecimal;

public record SkuItem(Long skuId, BigDecimal seckillPrice, int seckillCount, int limitPerUser) {
}
