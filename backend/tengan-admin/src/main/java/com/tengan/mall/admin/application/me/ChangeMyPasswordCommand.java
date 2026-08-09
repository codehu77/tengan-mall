package com.tengan.mall.admin.application.me;

public record ChangeMyPasswordCommand(Long adminId, String oldPassword, String newPassword) {
}
