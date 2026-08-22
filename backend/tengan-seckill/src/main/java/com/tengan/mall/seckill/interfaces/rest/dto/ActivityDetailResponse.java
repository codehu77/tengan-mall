package com.tengan.mall.seckill.interfaces.rest.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ActivityDetailResponse(Long id, String activityType, Instant startTime, Instant endTime,
        Long sessionId, LocalDate activityDate, String sessionName, String status, List<SkuResponse> skus) {
}
