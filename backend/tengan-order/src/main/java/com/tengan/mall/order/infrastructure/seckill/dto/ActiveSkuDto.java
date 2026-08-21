package com.tengan.mall.order.infrastructure.seckill.dto;

import java.math.BigDecimal;

public record ActiveSkuDto(Long skuId, Long activityId, BigDecimal seckillPrice, int limitPerUser) {
}
