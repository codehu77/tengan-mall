package com.tengan.mall.auth.application.oauth;

public record LinkFacebookAccountCommand(Long accountId, String accessToken) {
}
