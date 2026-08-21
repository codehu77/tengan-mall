package com.tengan.mall.seckill.interfaces.rest.dto;

import java.math.BigDecimal;

public record ActiveSkuResponse(Long skuId, Long activityId, BigDecimal seckillPrice, int limitPerUser) {
}
