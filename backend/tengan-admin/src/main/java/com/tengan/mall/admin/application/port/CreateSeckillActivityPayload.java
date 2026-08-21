package com.tengan.mall.admin.application.port;

import java.time.Instant;

public record CreateSeckillActivityPayload(String activityType, Instant startTime, Instant endTime) {
}
