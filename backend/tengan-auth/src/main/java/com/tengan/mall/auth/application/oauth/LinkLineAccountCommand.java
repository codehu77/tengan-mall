package com.tengan.mall.auth.application.oauth;

public record LinkLineAccountCommand(Long accountId, String code, String nonce) {
}
