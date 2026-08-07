package com.tengan.mall.member.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(@NotBlank String nickname, String avatarUrl) {
}
