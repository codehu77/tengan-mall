package com.tengan.mall.seckill.application.display;

import com.tengan.mall.seckill.domain.model.ActivityType;
import java.time.Instant;
import java.util.List;

public record ActiveActivityView(Long id, ActivityType activityType, Instant startTime, Instant endTime,
        List<ActiveSkuView> skus) {
}
