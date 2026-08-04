package com.tengan.mall.auth.application.register;

public record RegisterCommand(String username, String phone, String password, String code) {
}
