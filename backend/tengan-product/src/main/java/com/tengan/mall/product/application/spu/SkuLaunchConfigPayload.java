package com.tengan.mall.product.application.spu;

import java.time.LocalDateTime;

/** saleStartTime 是 SPU 級欄位，同一個 SPU 底下所有 SKU 這個值相同。 */
public record SkuLaunchConfigPayload(Long skuId, LocalDateTime saleStartTime, Integer purchaseLimitPerUser) {
}
