package com.tengan.mall.member.interfaces.rest.dto;

public record ProfileResponse(Long id, String phone, String email, String nickname, String avatarUrl) {
}
