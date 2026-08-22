package com.tengan.mall.seckill.interfaces.rest.dto;

import java.time.Instant;
import java.util.List;

public record PublicLaunchResponse(Long activityId, Instant startTime, Instant endTime,
        List<PublicSkuResponse> skus) {
}
