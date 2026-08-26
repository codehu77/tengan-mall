package com.tengan.mall.inventory.infrastructure.mq;

import java.time.LocalDateTime;
import java.util.List;

/** 跟 tengan-product 發布端（product-launch-config-exchange / product.launch-config.upserted）的 JSON 形狀對齊，不共用型別。 */
public record ProductLaunchConfigChangedEvent(Long spuId, List<SkuLaunchConfigItem> skus) {

    public record SkuLaunchConfigItem(Long skuId, LocalDateTime saleStartTime, Integer purchaseLimitPerUser) {
    }
}
