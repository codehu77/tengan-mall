package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.model.ActivityType;
import java.time.Instant;

public record CreateActivityCommand(ActivityType activityType, Instant startTime, Instant endTime) {
}
