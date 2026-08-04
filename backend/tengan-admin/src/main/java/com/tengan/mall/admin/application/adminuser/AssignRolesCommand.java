package com.tengan.mall.admin.application.adminuser;

import java.util.Set;

public record AssignRolesCommand(Long operatorId, String operatorUsername, Long targetId, Set<Long> roleIds) {
}
