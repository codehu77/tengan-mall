package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record LoginResponse(String accessToken, String refreshToken, Long adminId, String username,
        String realName, List<String> roleCodes, List<String> permissions) {
}
