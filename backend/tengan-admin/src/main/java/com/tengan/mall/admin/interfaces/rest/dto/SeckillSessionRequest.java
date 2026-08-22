package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record SeckillSessionRequest(@NotBlank String name, @NotNull LocalTime timeOfDay, int durationMinutes,
        int sortOrder, boolean enabled) {
}
