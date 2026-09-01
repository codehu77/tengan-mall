package com.tengan.mall.auth.interfaces.rest.dto;

public record ResetPasswordResponse(Long accountId, String accessToken, String refreshToken,
        long refreshTokenTtlSeconds) {
}
