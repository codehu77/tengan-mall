package com.tengan.mall.seckill.application.reservation;

import java.math.BigDecimal;

public record ActiveSkuStatus(Long skuId, Long activityId, BigDecimal seckillPrice, int limitPerUser) {
}
