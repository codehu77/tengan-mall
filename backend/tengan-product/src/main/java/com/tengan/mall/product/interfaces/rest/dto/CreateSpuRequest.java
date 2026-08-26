package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * skus 可以是空list——建立時先不填 sku 也合法，Spu 停在 NEW 狀態，之後用 update 補齊再 publish。
 * saleStartTime 以下 5 個欄位是「即將開賣」排程，全部可留 null/false（一般商品建立即可購買，不用管）。
 */
public record CreateSpuRequest(@NotNull Long categoryId, @NotNull Long brandId, @NotBlank String name,
        String description, String mainImage, LocalDateTime saleStartTime, boolean showOnLaunchTeaser,
        LocalDateTime teaserRemoveAt, @Valid List<SpuBaseAttrValueRequest> attrValues,
        @Valid List<SpuImageRequest> images, @Valid List<SkuRequest> skus) {
}
