package com.tengan.mall.admin.infrastructure.inventory.dto;

import java.time.LocalDateTime;

public record GateStatusItemEnvelope(Long skuId, LocalDateTime saleStartTime, LocalDateTime gateCloseTime,
        Integer purchaseLimitPerUser, Integer gateProtectedStock, LocalDateTime gateWarmedAt,
        LocalDateTime gateSettledAt, Integer currentAvailablePermits, Integer buyersCount) {
}
