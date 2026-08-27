package com.tengan.mall.inventory.interfaces.rest.dto;

import java.util.List;

public record StockSummaryResponse(List<SkuStockSummaryResponse> items) {
}
