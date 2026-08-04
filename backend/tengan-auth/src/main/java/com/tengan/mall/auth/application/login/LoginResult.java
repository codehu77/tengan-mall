package com.tengan.mall.auth.application.login;

public record LoginResult(String accessToken, String refreshToken, Long accountId, String username) {
}
