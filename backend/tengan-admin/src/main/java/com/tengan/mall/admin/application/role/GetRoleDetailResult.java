package com.tengan.mall.admin.application.role;

import java.util.List;

public record GetRoleDetailResult(Long id, String roleCode, String roleName, int status, List<Long> menuIds) {
}
