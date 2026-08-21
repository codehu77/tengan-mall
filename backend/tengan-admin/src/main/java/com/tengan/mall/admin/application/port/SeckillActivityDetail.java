package com.tengan.mall.admin.application.port;

import java.time.Instant;
import java.util.List;

public record SeckillActivityDetail(Long id, String activityType, Instant startTime, Instant endTime, String status,
        List<SeckillSkuItem> skus) {
}
