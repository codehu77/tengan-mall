package com.tengan.mall.inventory.application.stock;

public record CreateStockCommand(String operator, Long wareId, Long skuId, int initialStock) {
}
