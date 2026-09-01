package com.tengan.mall.auth.infrastructure.mq;

public record AccountContactChangedEvent(Long accountId, String phone, String email) {
}
