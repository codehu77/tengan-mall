package com.tengan.mall.admin.interfaces.rest.dto;

public record MemberSummaryResponse(Long id, String username, String phone, String nickname, String avatarUrl,
        int status) {
}
