package com.tengan.mall.seckill.domain.exception;

public class ActivityNotFoundException extends RuntimeException {

    public ActivityNotFoundException(Long activityId) {
        super("找不到秒殺活動: activityId=" + activityId);
    }
}
