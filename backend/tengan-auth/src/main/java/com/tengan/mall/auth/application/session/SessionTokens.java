package com.tengan.mall.auth.application.session;

public record SessionTokens(String accessToken, String refreshToken, long refreshTokenTtlSeconds) {
}
