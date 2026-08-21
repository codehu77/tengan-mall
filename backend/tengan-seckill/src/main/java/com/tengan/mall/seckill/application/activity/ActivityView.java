package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.model.ActivityStatus;
import com.tengan.mall.seckill.domain.model.ActivityType;
import java.time.Instant;

public record ActivityView(Long id, ActivityType activityType, Instant startTime, Instant endTime,
        ActivityStatus status) {
}
