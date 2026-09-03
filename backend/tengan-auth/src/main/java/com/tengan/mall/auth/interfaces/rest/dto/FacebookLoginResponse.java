package com.tengan.mall.auth.interfaces.rest.dto;

public record FacebookLoginResponse(Long accountId, String accessToken, String refreshToken,
        long refreshTokenTtlSeconds) {
}
