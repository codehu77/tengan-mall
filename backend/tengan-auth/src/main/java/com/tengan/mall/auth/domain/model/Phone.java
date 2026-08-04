package com.tengan.mall.auth.domain.model;

import java.util.regex.Pattern;

public record Phone(String value) {

    private static final Pattern TW_MOBILE = Pattern.compile("^09\\d{8}$");

    public Phone {
        if (value == null || !TW_MOBILE.matcher(value).matches()) {
            throw new IllegalArgumentException("phone 格式不正確，需為台灣手機號碼格式 09xxxxxxxx: " + value);
        }
    }
}
