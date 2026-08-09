package com.tengan.mall.inventory.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/** DB 存數字、Java 用 enum 包語意（docs/資料庫設計規範.md「enum/狀態欄位」）。 */
public enum WareTaskStatus implements IEnum<Integer> {

    LOCKED(1),
    RELEASED(2),
    DEDUCTED(3);

    @EnumValue
    private final int code;

    WareTaskStatus(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
