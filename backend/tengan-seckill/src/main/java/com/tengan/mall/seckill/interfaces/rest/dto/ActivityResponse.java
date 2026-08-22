package com.tengan.mall.seckill.interfaces.rest.dto;

import java.time.Instant;
import java.time.LocalDate;

public record ActivityResponse(Long id, String activityType, Instant startTime, Instant endTime, Long sessionId,
        LocalDate activityDate, String sessionName, String status) {
}
