package com.tengan.mall.seckill.infrastructure.redis;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * {@code seckill:sku:{skuId}} 的 JSON value 結構——這顆 SKU 目前活躍秒殺的唯一真相來源（見規劃第 3 節）。
 *
 * <p>startTime 是必要欄位：預熱排程會提前（最多 {@code warmup-horizon-hours}）把這個 key 寫進 Redis，
 * 目的是讓配額在真正開賣前就備妥，但 key 存在不代表「現在已經開賣」——呼叫端（{@code ReserveQuotaService}/
 * {@code CheckActiveSkusService}）都要額外比對 {@code Instant.now()} 是否已經到 startTime，
 * 避免使用者在還沒開賣時就搶先用秒殺價下單（真實 bug，見場次機制後續修正）。</p>
 */
public record ActiveSeckillInfo(Long activityId, BigDecimal seckillPrice, int limitPerUser, Instant startTime) {
}
