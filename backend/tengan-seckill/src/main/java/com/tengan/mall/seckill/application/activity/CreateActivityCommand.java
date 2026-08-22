package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.model.ActivityType;
import java.time.Instant;
import java.time.LocalDate;

/** FLASH_SALE 用 sessionId+activityDate；LAUNCH 用 startTime+endTime（另一組欄位為 null，見 CreateActivityService）。 */
public record CreateActivityCommand(ActivityType activityType, Long sessionId, LocalDate activityDate,
        Instant startTime, Instant endTime) {
}
