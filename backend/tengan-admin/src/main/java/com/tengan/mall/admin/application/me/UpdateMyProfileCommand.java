package com.tengan.mall.admin.application.me;

public record UpdateMyProfileCommand(Long adminId, String realName, String avatarUrl) {
}
