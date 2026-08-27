package com.tengan.mall.inventory.interfaces.rest.dto;

import java.time.LocalDateTime;

public record ConfigureGateRequest(LocalDateTime gateCloseTime) {
}
