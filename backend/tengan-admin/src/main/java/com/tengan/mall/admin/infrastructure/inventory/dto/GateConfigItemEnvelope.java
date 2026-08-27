package com.tengan.mall.admin.infrastructure.inventory.dto;

import java.time.LocalDateTime;

public record GateConfigItemEnvelope(Long spuId, LocalDateTime gateWarmedAt, LocalDateTime gateCloseTime) {
}
