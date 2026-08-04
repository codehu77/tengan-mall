package com.tengan.mall.auth.application.sms;

public record VerifySmsCodeCommand(String phone, String purpose, String code) {
}
