package com.tengan.mall.seckill.interfaces.rest.dto;

import java.time.Instant;
import java.util.List;

public record PublicActivityResponse(Long id, String activityType, Instant startTime, Instant endTime,
        List<PublicSkuResponse> skus) {
}
