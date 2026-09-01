package com.tengan.mall.auth.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record LinkGoogleAccountRequest(@NotBlank String idToken) {
}
