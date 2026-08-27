package com.tengan.mall.seckill.application.activity;

import java.time.LocalDate;

public record CreateActivityCommand(Long sessionId, LocalDate activityDate) {
}
