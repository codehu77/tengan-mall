package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeEmailStartRequest(@NotBlank String newEmail, String currentPassword) {
}
