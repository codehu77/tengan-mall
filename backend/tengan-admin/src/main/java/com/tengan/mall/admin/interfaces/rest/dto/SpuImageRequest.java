package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record SpuImageRequest(@NotBlank String imageUrl, int sort) {
}
