package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** 整批替換語意：skus/attrValues 帶的是「更新後的完整集合」，不是差異片段。 */
public record UpdateSpuRequest(@NotNull Long categoryId, @NotNull Long brandId, @NotBlank String name,
        String description, String mainImage, @Valid List<SpuBaseAttrValueRequest> attrValues,
        @Valid List<SpuImageRequest> images, @Valid List<SkuRequest> skus) {
}
