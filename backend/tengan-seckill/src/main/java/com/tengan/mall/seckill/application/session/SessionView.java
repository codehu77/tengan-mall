package com.tengan.mall.seckill.application.session;

import java.time.LocalTime;

public record SessionView(Long id, String name, LocalTime timeOfDay, int durationMinutes, int sortOrder,
        boolean enabled) {
}
