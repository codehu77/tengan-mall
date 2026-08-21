package com.tengan.mall.seckill.infrastructure.redis;

import java.math.BigDecimal;

/** {@code seckill:sku:{skuId}} 的 JSON value 結構——這顆 SKU 目前活躍秒殺的唯一真相來源（見規劃第 3 節）。 */
public record ActiveSeckillInfo(Long activityId, BigDecimal seckillPrice, int limitPerUser, String randomCode) {
}
