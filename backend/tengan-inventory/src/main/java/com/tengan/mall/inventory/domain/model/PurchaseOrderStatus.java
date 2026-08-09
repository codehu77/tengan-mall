package com.tengan.mall.inventory.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/** DB 存數字、Java 用 enum 包語意（docs/資料庫設計規範.md「enum/狀態欄位」）。 */
public enum PurchaseOrderStatus implements IEnum<Integer> {

    PENDING(1),
    RECEIVED(2);

    @EnumValue
    private final int code;

    PurchaseOrderStatus(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
