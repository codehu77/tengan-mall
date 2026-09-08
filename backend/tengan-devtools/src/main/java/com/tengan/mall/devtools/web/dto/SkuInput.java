package com.tengan.mall.devtools.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record SkuInput(@NotBlank String name, @NotNull @Positive BigDecimal price, String imageUrl,
        @Valid List<AttrValueInput> saleAttrValues, @Positive Integer purchaseLimitPerUser) {
}
