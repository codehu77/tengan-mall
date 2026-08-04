package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record AdminUserDetailResponse(Long id, String username, String realName, int status,
        List<Long> roleIds) {
}
