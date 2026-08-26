package com.tengan.mall.admin.interfaces.rest.dto;

import java.time.LocalDateTime;

public record GateStatusResponse(Long skuId, Long spuId, String skuName, String mainImage,
        LocalDateTime saleStartTime, LocalDateTime gateCloseTime, Integer purchaseLimitPerUser,
        Integer gateProtectedStock, LocalDateTime gateWarmedAt, LocalDateTime gateSettledAt,
        Integer currentAvailablePermits, Integer buyersCount) {
}
