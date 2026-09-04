package com.tengan.mall.auth.application.oauth;

public record LineLoginResult(Long accountId, String accessToken, String refreshToken,
        long refreshTokenTtlSeconds) {
}
