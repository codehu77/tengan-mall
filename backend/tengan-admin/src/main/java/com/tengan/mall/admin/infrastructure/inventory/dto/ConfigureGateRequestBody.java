package com.tengan.mall.admin.infrastructure.inventory.dto;

import java.time.LocalDateTime;

public record ConfigureGateRequestBody(LocalDateTime gateCloseTime) {
}
