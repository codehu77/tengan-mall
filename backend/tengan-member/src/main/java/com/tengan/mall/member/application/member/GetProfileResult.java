package com.tengan.mall.member.application.member;

public record GetProfileResult(Long id, String username, String phone, String nickname, String avatarUrl) {
}
