package com.tengan.mall.seckill.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 2026-08-27：原本還有一個 LAUNCH(2)（首發/流量閘門）類型，跟 FLASH_SALE 共用同一套「時間窗+
 * 配額+高併發保護」骨架，只是 seckillCount 的解讀方式不同（LAUNCH 是節流參考值，不是真實庫存上限）。
 * 已經被獨立的「即將開賣」+「防超賣保護」功能（tengan-product/tengan-inventory）取代並移除——
 * `activity_type` 欄位保留（歷史 migration 已套用，不回頭改），但新資料不會再出現 code=2。
 */
public enum ActivityType implements IEnum<Integer> {

    FLASH_SALE(1);

    @EnumValue
    private final int code;

    ActivityType(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    public static ActivityType fromCode(int code) {
        for (var value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知的 ActivityType code: " + code);
    }
}
