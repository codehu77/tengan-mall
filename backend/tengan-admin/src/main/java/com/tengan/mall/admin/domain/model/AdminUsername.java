package com.tengan.mall.admin.domain.model;

public record AdminUsername(String value) {

    public AdminUsername {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("username 不可為空");
        }
        if (value.length() < 4 || value.length() > 50) {
            throw new IllegalArgumentException("username 長度需介於 4~50 字元");
        }
    }
}
