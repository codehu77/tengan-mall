package com.tengan.mall.auth.application.oauth;

public record GoogleLoginResult(Long accountId, String accessToken, String refreshToken,
        long refreshTokenTtlSeconds) {
}
