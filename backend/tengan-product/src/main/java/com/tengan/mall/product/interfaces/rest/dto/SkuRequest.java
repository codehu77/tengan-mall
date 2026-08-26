package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

/** purchaseLimitPerUser 為 null 代表不限購。id 為 null 代表新規格，非 null 代表編輯既有規格
 * （後端會驗證這個 id 真的屬於這個 SPU，見 SpuCompositionAssembler）。 */
public record SkuRequest(Long id, @NotBlank String name, @NotNull @Positive BigDecimal price, String mainImage,
        int sort, @Valid List<SkuImageRequest> images, @Valid List<SkuSaleAttrValueRequest> saleAttrValues,
        @Positive Integer purchaseLimitPerUser) {
}
