package com.tengan.mall.seckill.interfaces.rest.dto;

import java.math.BigDecimal;

public record ReserveResponse(Long activityId, BigDecimal seckillPrice) {
}
