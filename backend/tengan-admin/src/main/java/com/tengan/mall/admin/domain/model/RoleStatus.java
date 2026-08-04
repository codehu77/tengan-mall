package com.tengan.mall.admin.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum RoleStatus implements IEnum<Integer> {

    DISABLED(0),
    ACTIVE(1);

    @EnumValue
    private final int code;

    RoleStatus(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
