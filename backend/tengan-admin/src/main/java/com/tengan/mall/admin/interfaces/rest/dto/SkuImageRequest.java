package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record SkuImageRequest(@NotBlank String imageUrl, int sort) {
}
