package com.tengan.mall.admin.application.role;

public record UpdateRoleCommand(Long operatorId, String operatorUsername, Long id, String roleName, boolean active) {
}
