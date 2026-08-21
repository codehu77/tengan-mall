package com.tengan.mall.seckill.interfaces.rest.dto;

import java.time.Instant;

public record ActivityResponse(Long id, String activityType, Instant startTime, Instant endTime, String status) {
}
