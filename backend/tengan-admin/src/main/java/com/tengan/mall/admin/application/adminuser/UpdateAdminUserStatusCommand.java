package com.tengan.mall.admin.application.adminuser;

public record UpdateAdminUserStatusCommand(Long operatorId, String operatorUsername, Long targetId, boolean active) {
}
