package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.model.ActivityStatus;
import com.tengan.mall.seckill.domain.model.ActivityType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ActivityDetailView(Long id, ActivityType activityType, Instant startTime, Instant endTime,
        Long sessionId, LocalDate activityDate, String sessionName, ActivityStatus status, List<SkuView> skus) {
}
