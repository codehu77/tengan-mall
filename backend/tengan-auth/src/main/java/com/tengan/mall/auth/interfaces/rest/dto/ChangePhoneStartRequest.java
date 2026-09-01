package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePhoneStartRequest(@NotBlank String newPhone, String currentPassword) {
}
