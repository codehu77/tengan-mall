package com.tengan.mall.auth.application.password;

public record ForgotPasswordVerifyCommand(String identifier, String code) {
}
