package com.tengan.mall.seckill.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 限時搶購跟首發合併成同一套機制後，用這個欄位參數化「配額是不是真的稀缺」：
 * FLASH_SALE 的 {@code seckillCount} 是真正的可售上限，賣完就是真的沒了；LAUNCH（首發）的
 * {@code seckillCount} 是節流用的放行速率參考值，不代表庫存上限，實際庫存另外查 tengan-inventory。
 * 兩者共用同一套「時間窗+配額+高併發保護」骨架，差異只在這個欄位怎麼被解讀，不需要另開新服務
 * （見 Phase 9 規劃 Context 第 2 點）。
 */
public enum ActivityType implements IEnum<Integer> {

    FLASH_SALE(1),
    LAUNCH(2);

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
