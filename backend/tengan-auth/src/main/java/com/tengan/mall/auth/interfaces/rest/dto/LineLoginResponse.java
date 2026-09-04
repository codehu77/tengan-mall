package com.tengan.mall.auth.interfaces.rest.dto;

public record LineLoginResponse(Long accountId, String accessToken, String refreshToken,
        long refreshTokenTtlSeconds) {
}
