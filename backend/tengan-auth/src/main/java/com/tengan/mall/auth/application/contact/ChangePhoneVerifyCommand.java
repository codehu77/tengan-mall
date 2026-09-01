package com.tengan.mall.auth.application.contact;

public record ChangePhoneVerifyCommand(Long accountId, String newPhone, String code) {
}
