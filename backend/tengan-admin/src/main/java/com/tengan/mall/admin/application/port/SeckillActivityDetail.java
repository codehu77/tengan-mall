package com.tengan.mall.admin.application.port;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record SeckillActivityDetail(Long id, String activityType, Instant startTime, Instant endTime,
        Long sessionId, LocalDate activityDate, String sessionName, String status, List<SeckillSkuItem> skus) {
}
