package com.tengan.mall.admin.interfaces.rest.dto;

import java.time.LocalTime;

public record SeckillSessionResponse(Long id, String name, LocalTime timeOfDay, int durationMinutes, int sortOrder,
        boolean enabled) {
}
