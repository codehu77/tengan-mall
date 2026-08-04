package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAdminUserRequest(@NotBlank String username, @NotBlank String password, String realName) {
}
