package com.tengan.mall.auth.application.contact;

public record ChangeEmailVerifyCommand(Long accountId, String newEmail, String code) {
}
