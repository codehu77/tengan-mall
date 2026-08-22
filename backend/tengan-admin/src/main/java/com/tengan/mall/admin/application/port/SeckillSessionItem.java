package com.tengan.mall.admin.application.port;

import java.time.LocalTime;

public record SeckillSessionItem(Long id, String name, LocalTime timeOfDay, int durationMinutes, int sortOrder,
        boolean enabled) {
}
