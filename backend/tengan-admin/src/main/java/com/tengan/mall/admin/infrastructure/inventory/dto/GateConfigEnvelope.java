package com.tengan.mall.admin.infrastructure.inventory.dto;

import java.time.LocalDateTime;

public record GateConfigEnvelope(Long skuId, boolean synced, boolean trafficGateEnabled,
        LocalDateTime saleStartTime, LocalDateTime gateCloseTime, LocalDateTime gateWarmedAt,
        LocalDateTime gateSettledAt) {
}
