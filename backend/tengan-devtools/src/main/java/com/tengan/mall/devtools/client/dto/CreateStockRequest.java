package com.tengan.mall.devtools.client.dto;

public record CreateStockRequest(Long skuId, Long wareId, int initialStock) {
}
