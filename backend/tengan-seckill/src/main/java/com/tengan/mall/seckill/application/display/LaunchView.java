package com.tengan.mall.seckill.application.display;

import java.time.Instant;
import java.util.List;

/** LAUNCH 不走場次，只有 ACTIVE 才會出現在這裡（沿用原本 findActive() 的邏輯）。 */
public record LaunchView(Long activityId, Instant startTime, Instant endTime, List<ActiveProductView> products) {
}
