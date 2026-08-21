package com.tengan.mall.seckill.domain.exception;

import com.tengan.mall.seckill.domain.model.ActivityStatus;

public class ActivityStatusTransitionNotAllowedException extends RuntimeException {

    public ActivityStatusTransitionNotAllowedException(Long activityId, ActivityStatus current,
            ActivityStatus target) {
        super("activityId=" + activityId + " 目前狀態 " + current + "，不允許轉換到 " + target);
    }
}
