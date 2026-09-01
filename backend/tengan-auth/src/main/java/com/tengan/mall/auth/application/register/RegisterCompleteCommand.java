package com.tengan.mall.auth.application.register;

public record RegisterCompleteCommand(String registrationToken, String password) {
}
