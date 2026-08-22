package com.tengan.mall.admin.interfaces.rest.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record SeckillActivityDetailResponse(Long id, String activityType, Instant startTime, Instant endTime,
        Long sessionId, LocalDate activityDate, String sessionName, String status, List<SeckillSkuResponse> skus) {
}
