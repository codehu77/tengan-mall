package com.tengan.mall.auth.application.contact;

public record ChangeEmailStartCommand(Long accountId, String newEmail, String currentPassword) {
}
