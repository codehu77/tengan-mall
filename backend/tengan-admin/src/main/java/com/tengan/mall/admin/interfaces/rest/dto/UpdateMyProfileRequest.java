package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateMyProfileRequest(@NotBlank String realName, String avatarUrl) {
}
