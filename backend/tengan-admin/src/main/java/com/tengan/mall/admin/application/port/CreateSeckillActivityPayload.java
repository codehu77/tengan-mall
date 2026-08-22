package com.tengan.mall.admin.application.port;

import java.time.Instant;
import java.time.LocalDate;

/** FLASH_SALE 帶 sessionId+activityDate；LAUNCH 帶 startTime+endTime（另一組為 null）。 */
public record CreateSeckillActivityPayload(String activityType, Long sessionId, LocalDate activityDate,
        Instant startTime, Instant endTime) {
}
