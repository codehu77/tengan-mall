package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateSpuRequest(@NotNull Long categoryId, @NotNull Long brandId, @NotBlank String name,
        String description, String mainImage, @Valid List<SpuBaseAttrValueRequest> attrValues,
        @Valid List<SpuImageRequest> images, @Valid List<SkuRequest> skus) {
}
