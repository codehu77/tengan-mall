package com.tengan.mall.admin.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum MenuType implements IEnum<Integer> {

    CATALOG(1),
    MENU(2),
    BUTTON(3);

    @EnumValue
    private final int code;

    MenuType(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    public static MenuType fromCode(int code) {
        for (MenuType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的 menuType code: " + code);
    }
}
