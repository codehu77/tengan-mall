package com.tengan.mall.admin.application.port;

import java.time.LocalDateTime;

public record ConfigureGatePayload(boolean trafficGateEnabled, LocalDateTime gateCloseTime) {
}
