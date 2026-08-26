package com.tengan.mall.inventory.interfaces.rest.dto;

import java.time.LocalDateTime;

public record GateStatusResponse(Long skuId, LocalDateTime saleStartTime, LocalDateTime gateCloseTime,
        Integer purchaseLimitPerUser, Integer gateProtectedStock, LocalDateTime gateWarmedAt,
        LocalDateTime gateSettledAt, Integer currentAvailablePermits, Integer buyersCount) {
}
