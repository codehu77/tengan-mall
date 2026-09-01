package com.tengan.mall.member.application.member;

public record RegisterMemberFromEventCommand(Long memberId, String phone, String email, String nickname,
        String avatarUrl) {
}
