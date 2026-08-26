package com.tengan.mall.inventory.domain.model;

import java.time.LocalDateTime;

/** 從 tengan-product 單向同步過來的本地副本，只保留 lock() 校驗時要用的欄位。 */
public record SkuLaunchConfig(Long skuId, LocalDateTime saleStartTime, Integer purchaseLimitPerUser) {
}
