package com.tengan.mall.member.application.member;

public record GetProfileResult(Long id, String phone, String email, String nickname, String avatarUrl) {
}
