package com.tengan.mall.order.application.port;

import java.math.BigDecimal;

/** 呼叫 tengan-seckill 的 /internal/seckill/skus/batch-status 回應的扁平投影。 */
public record ActiveSeckillSku(Long activityId, BigDecimal seckillPrice, int limitPerUser) {
}
