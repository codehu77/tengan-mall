package com.tengan.mall.seckill.interfaces.rest.dto;

import java.time.Instant;
import java.util.List;

public record PublicFlashSaleSessionResponse(Long activityId, Long sessionId, String sessionName, Instant startTime,
        Instant endTime, String status, List<PublicProductResponse> products) {
}
