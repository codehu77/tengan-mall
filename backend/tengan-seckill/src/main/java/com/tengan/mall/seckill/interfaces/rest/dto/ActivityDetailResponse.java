package com.tengan.mall.seckill.interfaces.rest.dto;

import java.time.Instant;
import java.util.List;

public record ActivityDetailResponse(Long id, String activityType, Instant startTime, Instant endTime,
        String status, List<SkuResponse> skus) {
}
