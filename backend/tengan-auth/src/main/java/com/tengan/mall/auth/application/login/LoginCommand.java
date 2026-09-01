package com.tengan.mall.auth.application.login;

public record LoginCommand(String identifier, String password, boolean rememberMe) {
}
