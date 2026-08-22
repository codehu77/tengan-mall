package com.tengan.mall.seckill.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDate;

/** FLASH_SALE 填 sessionId+activityDate；LAUNCH 填 startTime+endTime——依 activityType 由 controller 決定要驗證哪一組（見 InternalSeckillController）。 */
public record CreateActivityRequest(@NotBlank String activityType, Long sessionId, LocalDate activityDate,
        Instant startTime, Instant endTime) {
}
