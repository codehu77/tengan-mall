package com.tengan.mall.admin.application.adminuser;

public record CreateAdminUserCommand(Long operatorId, String operatorUsername, String username, String password,
        String realName) {
}
