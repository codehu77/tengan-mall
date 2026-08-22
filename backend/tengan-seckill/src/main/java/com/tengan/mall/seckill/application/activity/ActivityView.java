package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.model.ActivityStatus;
import com.tengan.mall.seckill.domain.model.ActivityType;
import java.time.Instant;
import java.time.LocalDate;

/** sessionId/activityDate/sessionName 只有 FLASH_SALE 有值，LAUNCH 皆為 null。 */
public record ActivityView(Long id, ActivityType activityType, Instant startTime, Instant endTime, Long sessionId,
        LocalDate activityDate, String sessionName, ActivityStatus status) {
}
