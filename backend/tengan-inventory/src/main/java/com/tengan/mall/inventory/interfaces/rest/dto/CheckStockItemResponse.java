package com.tengan.mall.inventory.interfaces.rest.dto;

public record CheckStockItemResponse(Long skuId, boolean sufficient, int availableStock) {
}
