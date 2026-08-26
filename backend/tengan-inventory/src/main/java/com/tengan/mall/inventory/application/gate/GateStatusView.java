package com.tengan.mall.inventory.application.gate;

import java.time.LocalDateTime;

/**
 * currentAvailablePermits/buyersCount 只有「已預熱、還沒結算」時才有意義（這段期間 Redis 資料才是
 * 活著的），其餘狀態一律回 null，前端顯示「—」。
 */
public record GateStatusView(Long skuId, LocalDateTime saleStartTime, LocalDateTime gateCloseTime,
        Integer purchaseLimitPerUser, Integer gateProtectedStock, LocalDateTime gateWarmedAt,
        LocalDateTime gateSettledAt, Integer currentAvailablePermits, Integer buyersCount) {
}
