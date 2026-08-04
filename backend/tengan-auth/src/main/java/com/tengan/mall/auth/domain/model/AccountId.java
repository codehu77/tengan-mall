package com.tengan.mall.auth.domain.model;

public record AccountId(Long value) {

    public AccountId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("AccountId 必須是正整數: " + value);
        }
    }
}
