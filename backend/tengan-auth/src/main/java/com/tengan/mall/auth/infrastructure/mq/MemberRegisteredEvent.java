package com.tengan.mall.auth.infrastructure.mq;

public record MemberRegisteredEvent(Long memberId, String phone, String email, String nickname, String avatarUrl) {
}
