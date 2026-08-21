package com.tengan.mall.admin.interfaces.rest.dto;

import java.time.Instant;

public record SeckillActivityResponse(Long id, String activityType, Instant startTime, Instant endTime,
        String status) {
}
