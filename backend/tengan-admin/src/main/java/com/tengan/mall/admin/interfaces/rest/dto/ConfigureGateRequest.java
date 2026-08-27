package com.tengan.mall.admin.interfaces.rest.dto;

import java.time.LocalDateTime;

public record ConfigureGateRequest(LocalDateTime gateCloseTime) {
}
