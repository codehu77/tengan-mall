package com.tengan.mall.auth.interfaces.rest.dto;

public record RegisterCompleteResponse(Long accountId, String accessToken, String refreshToken,
        long refreshTokenTtlSeconds) {
}
