package com.tengan.mall.wallet.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 只用在 EARN 的 reserve→confirm 生命週期（PENDING→CONFIRMED）跟 REDEEM 被取消訂單撤銷
 * （CONFIRMED→REVERSED）。EXPIRE/ADJUST 一律直接以 CONFIRMED 寫入、終生不轉換——到期是另開一筆
 * 負向 EXPIRE 列沖銷原本的 EARN 列，不是把 EARN 列自己的狀態改掉（見 PointsTransaction 類別說明）。
 */
public enum PointsTransactionStatus implements IEnum<Integer> {

    PENDING(1),
    CONFIRMED(2),
    REVERSED(3);

    @EnumValue
    private final int code;

    PointsTransactionStatus(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    public static PointsTransactionStatus fromCode(int code) {
        for (var value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知的 PointsTransactionStatus code: " + code);
    }
}
