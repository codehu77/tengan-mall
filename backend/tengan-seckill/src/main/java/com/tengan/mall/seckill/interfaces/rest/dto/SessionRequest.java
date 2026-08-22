package com.tengan.mall.seckill.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record SessionRequest(@NotBlank String name, @NotNull LocalTime timeOfDay, int durationMinutes,
        int sortOrder, boolean enabled) {
}
