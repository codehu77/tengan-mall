package com.tengan.mall.order.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/** DB 存數字、Java 用 enum 包語意（docs/資料庫設計規範.md「enum/狀態欄位」）。 */
public enum OrderStatus implements IEnum<Integer> {

    PENDING_PAYMENT(1),
    PAID(2),
    SHIPPED(3),
    COMPLETED(4),
    CANCELLED(5);

    @EnumValue
    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
