package com.tengan.mall.auth.application.password;

public record ResetPasswordCommand(String resetToken, String newPassword) {
}
