package com.tengan.mall.admin.application.port;

import java.time.Instant;
import java.time.LocalDate;

public record SeckillActivityItem(Long id, String activityType, Instant startTime, Instant endTime, Long sessionId,
        LocalDate activityDate, String sessionName, String status) {
}
