package com.tengan.mall.admin.interfaces.rest.dto;

public record MemberSummaryResponse(Long id, String phone, String email, String nickname, String avatarUrl,
        int status) {
}
