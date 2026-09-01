package com.tengan.mall.auth.application.contact;

public record ChangePhoneStartCommand(Long accountId, String newPhone, String currentPassword) {
}
