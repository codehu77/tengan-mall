package com.tengan.mall.admin.application.login;

import java.util.List;

public record AdminLoginResult(String accessToken, String refreshToken, Long adminId, String username,
        String realName, List<String> roleCodes, List<String> permissions) {
}
