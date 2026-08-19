package com.tengan.mall.payment.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/** DB 存數字、Java 用 enum 包語意（docs/資料庫設計規範.md「enum/狀態欄位」）。 */
public enum PaymentStatus implements IEnum<Integer> {

    PENDING(1),
    PAID(2),
    /** 同步查帳（使用者重試觸發/排程掃描）確認這筆嘗試沒有真的付款成功，結案存查。 */
    FAILED(3);

    @EnumValue
    private final int code;

    PaymentStatus(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
