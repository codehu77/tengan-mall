package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record BannerRequest(@NotBlank String imageUrl, String linkUrl, String title, int sortOrder,
        boolean enabled) {
}
