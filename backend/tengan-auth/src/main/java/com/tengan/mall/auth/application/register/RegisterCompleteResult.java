package com.tengan.mall.auth.application.register;

public record RegisterCompleteResult(Long accountId, String accessToken, String refreshToken,
        long refreshTokenTtlSeconds) {
}
