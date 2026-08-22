package com.tengan.mall.seckill.interfaces.rest.dto;

import java.time.LocalTime;

public record SessionResponse(Long id, String name, LocalTime timeOfDay, int durationMinutes, int sortOrder,
        boolean enabled) {
}
