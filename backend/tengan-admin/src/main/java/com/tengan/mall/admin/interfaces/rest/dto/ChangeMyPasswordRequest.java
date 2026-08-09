package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeMyPasswordRequest(@NotBlank String oldPassword, @NotBlank @Size(min = 8) String newPassword) {
}
