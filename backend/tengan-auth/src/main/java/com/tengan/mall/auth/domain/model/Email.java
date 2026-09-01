package com.tengan.mall.auth.domain.model;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern SIMPLE_EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value == null || !SIMPLE_EMAIL.matcher(value).matches()) {
            throw new IllegalArgumentException("email 格式不正確: " + value);
        }
    }
}
