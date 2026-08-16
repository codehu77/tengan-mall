package com.tengan.mall.wallet.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/** DB 存數字、Java 用 enum 包語意（docs/資料庫設計規範.md「enum/狀態欄位」）。不含 REFUND（退款刻意不做定案）。 */
public enum PointsTransactionType implements IEnum<Integer> {

    EARN(1),
    REDEEM(2),
    EXPIRE(3),
    ADJUST(4);

    @EnumValue
    private final int code;

    PointsTransactionType(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    public static PointsTransactionType fromCode(int code) {
        for (var value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知的 PointsTransactionType code: " + code);
    }
}
