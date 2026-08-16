package com.tengan.mall.wallet.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum MemberTierLevel implements IEnum<Integer> {

    FREE(1),
    PRO(2),
    PRO_PLUS(3);

    @EnumValue
    private final int code;

    MemberTierLevel(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
