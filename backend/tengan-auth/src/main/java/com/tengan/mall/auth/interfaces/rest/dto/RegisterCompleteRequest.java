package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterCompleteRequest(
        @NotBlank String registrationToken,
        @NotBlank @Size(min = 6) String password) {
}
