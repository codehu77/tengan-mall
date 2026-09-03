package com.tengan.mall.auth.application.oauth;

public record FacebookLoginResult(Long accountId, String accessToken, String refreshToken,
        long refreshTokenTtlSeconds) {
}
