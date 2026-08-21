package com.tengan.mall.admin.interfaces.rest.dto;

import java.time.Instant;
import java.util.List;

public record SeckillActivityDetailResponse(Long id, String activityType, Instant startTime, Instant endTime,
        String status, List<SeckillSkuResponse> skus) {
}
