package com.tengan.mall.auth.application.password;

public record ResetPasswordResult(Long accountId, String accessToken, String refreshToken,
        long refreshTokenTtlSeconds) {
}
