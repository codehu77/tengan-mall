package com.tengan.mall.admin.application.adminuser;

import java.util.List;

public record GetAdminUserDetailResult(Long id, String username, String realName, int status,
        List<Long> roleIds) {
}
