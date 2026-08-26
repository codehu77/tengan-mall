package com.tengan.mall.product.application.spu;

import java.time.LocalDateTime;

/** saleStartTime/trafficGateEnabled/gateCloseTime 是 SPU 級欄位，同一個 SPU 底下所有 SKU 這幾個值相同。 */
public record SkuLaunchConfigPayload(Long skuId, LocalDateTime saleStartTime, boolean trafficGateEnabled,
        LocalDateTime gateCloseTime, Integer purchaseLimitPerUser) {
}
