package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateRoleRequest(@NotBlank String roleName, boolean active) {
}
