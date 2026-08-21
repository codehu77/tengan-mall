package com.tengan.mall.seckill.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * DRAFT（後台建立、還沒排入預熱）→ PUBLISHED（後台確認可以排程）→ ACTIVE（預熱排程處理過，
 * Redis 配額已就緒）→ SETTLED（結算完成，真實庫存已同步）。只有 PUBLISHED 才會被
 * {@link com.tengan.mall.seckill.application.warmup.WarmUpActivitiesUseCase} 撈出來預熱，
 * 只有 ACTIVE 才會被結算排程撈出來結算，避免排程重複處理同一個活動。
 */
public enum ActivityStatus implements IEnum<Integer> {

    DRAFT(1),
    PUBLISHED(2),
    ACTIVE(3),
    SETTLED(4);

    @EnumValue
    private final int code;

    ActivityStatus(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    public static ActivityStatus fromCode(int code) {
        for (var value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知的 ActivityStatus code: " + code);
    }
}
