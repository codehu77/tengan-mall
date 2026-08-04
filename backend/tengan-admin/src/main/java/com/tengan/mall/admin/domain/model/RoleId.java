package com.tengan.mall.admin.domain.model;

public record RoleId(Long value) {

    public RoleId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("RoleId 必須是正整數: " + value);
        }
    }
}
