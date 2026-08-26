package com.tengan.mall.admin.interfaces.rest.dto;

import java.time.LocalDateTime;

public record GateConfigResponse(Long skuId, boolean synced, boolean trafficGateEnabled,
        LocalDateTime saleStartTime, LocalDateTime gateCloseTime, LocalDateTime gateWarmedAt,
        LocalDateTime gateSettledAt) {
}
