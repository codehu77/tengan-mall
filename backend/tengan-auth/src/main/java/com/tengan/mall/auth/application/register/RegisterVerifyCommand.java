package com.tengan.mall.auth.application.register;

public record RegisterVerifyCommand(String identifier, String code) {
}
