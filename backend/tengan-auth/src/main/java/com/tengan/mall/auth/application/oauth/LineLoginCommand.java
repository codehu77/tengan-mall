package com.tengan.mall.auth.application.oauth;

public record LineLoginCommand(String code, String nonce) {
}
