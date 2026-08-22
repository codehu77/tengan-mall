package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDate;

/** FLASH_SALE 填 sessionId+activityDate；LAUNCH 填 startTime+endTime——後端 tengan-seckill 依 activityType 驗證，這裡不重複做。 */
public record CreateSeckillActivityRequest(@NotBlank String activityType, Long sessionId, LocalDate activityDate,
        Instant startTime, Instant endTime) {
}
