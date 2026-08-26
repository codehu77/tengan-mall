package com.tengan.mall.inventory.interfaces.rest.dto;

import java.time.LocalDateTime;

public record ConfigureGateRequest(boolean trafficGateEnabled, LocalDateTime gateCloseTime) {
}
