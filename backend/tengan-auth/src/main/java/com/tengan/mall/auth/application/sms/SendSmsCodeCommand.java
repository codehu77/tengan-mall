package com.tengan.mall.auth.application.sms;

public record SendSmsCodeCommand(String phone, String purpose) {
}
