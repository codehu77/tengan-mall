package com.tengan.mall.admin.application.role;

public record CreateRoleCommand(Long operatorId, String operatorUsername, String roleCode, String roleName) {
}
