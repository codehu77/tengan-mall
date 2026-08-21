package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateSeckillActivityRequest(@NotBlank String activityType, @NotNull Instant startTime,
        @NotNull Instant endTime) {
}
