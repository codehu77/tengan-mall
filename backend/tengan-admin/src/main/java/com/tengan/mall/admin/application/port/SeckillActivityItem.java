package com.tengan.mall.admin.application.port;

import java.time.Instant;

public record SeckillActivityItem(Long id, String activityType, Instant startTime, Instant endTime, String status) {
}
