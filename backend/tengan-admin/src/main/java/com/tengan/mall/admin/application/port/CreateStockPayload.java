package com.tengan.mall.admin.application.port;

public record CreateStockPayload(Long skuId, Long wareId, int initialStock) {
}
