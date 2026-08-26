package com.tengan.mall.admin.application.port;

import java.time.LocalDateTime;

public record GateStatusItem(Long skuId, LocalDateTime saleStartTime, LocalDateTime gateCloseTime,
        Integer purchaseLimitPerUser, Integer gateProtectedStock, LocalDateTime gateWarmedAt,
        LocalDateTime gateSettledAt, Integer currentAvailablePermits, Integer buyersCount) {
}
