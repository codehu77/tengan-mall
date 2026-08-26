package com.tengan.mall.inventory.interfaces.rest.dto;

import java.time.LocalDateTime;

public record SkuStockResponse(Long skuId, int availableStock, LocalDateTime saleStartTime, boolean purchasable,
        Integer purchaseLimitPerUser) {
}
