package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record MeResponse(Long adminId, String username, String realName, List<String> roleCodes) {
}
