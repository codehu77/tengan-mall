package com.tengan.mall.admin.domain.model;

public record AdminUserId(Long value) {

    public AdminUserId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("AdminUserId 必須是正整數: " + value);
        }
    }
}
