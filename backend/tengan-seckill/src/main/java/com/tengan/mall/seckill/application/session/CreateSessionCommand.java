package com.tengan.mall.seckill.application.session;

import java.time.LocalTime;

public record CreateSessionCommand(String name, LocalTime timeOfDay, int durationMinutes, int sortOrder,
        boolean enabled) {
}
