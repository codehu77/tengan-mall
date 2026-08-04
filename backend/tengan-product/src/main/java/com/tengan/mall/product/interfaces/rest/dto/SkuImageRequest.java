package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record SkuImageRequest(@NotBlank String imageUrl, int sort) {
}
