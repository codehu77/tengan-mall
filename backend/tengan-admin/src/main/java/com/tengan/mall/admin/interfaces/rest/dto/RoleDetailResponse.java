package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record RoleDetailResponse(Long id, String roleCode, String roleName, int status, List<Long> menuIds) {
}
