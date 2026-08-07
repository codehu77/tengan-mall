package com.tengan.mall.member.application.member;

public record UpdateProfileCommand(Long memberId, String nickname, String avatarUrl) {
}
