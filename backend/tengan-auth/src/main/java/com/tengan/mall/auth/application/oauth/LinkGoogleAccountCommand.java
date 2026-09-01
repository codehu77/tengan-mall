package com.tengan.mall.auth.application.oauth;

public record LinkGoogleAccountCommand(Long accountId, String idToken) {
}
