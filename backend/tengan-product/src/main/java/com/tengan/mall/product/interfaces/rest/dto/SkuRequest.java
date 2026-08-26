package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

/** purchaseLimitPerUser 為 null 代表不限購。 */
public record SkuRequest(@NotBlank String name, @NotNull @Positive BigDecimal price, String mainImage, int sort,
        @Valid List<SkuImageRequest> images, @Valid List<SkuSaleAttrValueRequest> saleAttrValues,
        @Positive Integer purchaseLimitPerUser) {
}
