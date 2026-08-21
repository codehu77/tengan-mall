package com.tengan.mall.seckill.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateActivityRequest(@NotBlank String activityType, @NotNull Instant startTime,
        @NotNull Instant endTime) {
}
