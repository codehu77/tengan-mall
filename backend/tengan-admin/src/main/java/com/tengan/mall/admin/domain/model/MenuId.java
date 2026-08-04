package com.tengan.mall.admin.domain.model;

public record MenuId(Long value) {

    public MenuId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("MenuId 必須是正整數: " + value);
        }
    }
}
