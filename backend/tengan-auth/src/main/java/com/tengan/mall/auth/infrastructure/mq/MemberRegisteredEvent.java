package com.tengan.mall.auth.infrastructure.mq;

public record MemberRegisteredEvent(Long memberId, String username, String phone) {
}
