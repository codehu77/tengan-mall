package com.tengan.mall.auth.interfaces.rest.dto;

public record GoogleLoginResponse(Long accountId, String accessToken, String refreshToken,
        long refreshTokenTtlSeconds) {
}
