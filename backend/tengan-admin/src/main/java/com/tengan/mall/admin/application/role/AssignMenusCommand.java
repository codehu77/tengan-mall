package com.tengan.mall.admin.application.role;

import java.util.Set;

public record AssignMenusCommand(Long operatorId, String operatorUsername, Long roleId, Set<Long> menuIds) {
}
