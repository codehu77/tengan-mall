package com.tengan.mall.admin.application.port;

import java.time.LocalTime;

public record SeckillSessionPayload(String name, LocalTime timeOfDay, int durationMinutes, int sortOrder,
        boolean enabled) {
}
