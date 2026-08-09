package com.tengan.mall.inventory.interfaces.rest.dto;

import java.util.List;

public record ListSkuStockResponse(List<WareSkuStockResponse> items, long total) {
}
