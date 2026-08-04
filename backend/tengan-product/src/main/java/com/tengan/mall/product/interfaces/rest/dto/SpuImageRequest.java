package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record SpuImageRequest(@NotBlank String imageUrl, int sort) {
}
