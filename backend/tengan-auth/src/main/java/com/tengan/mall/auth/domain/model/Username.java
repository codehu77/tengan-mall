package com.tengan.mall.auth.domain.model;

public record Username(String value) {

    public Username {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("username 不可為空");
        }
        if (value.length() < 4 || value.length() > 50) {
            throw new IllegalArgumentException("username 長度需介於 4~50 字元");
        }
    }
}
