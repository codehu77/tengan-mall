package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRoleRequest(@NotBlank String roleCode, @NotBlank String roleName) {
}
