package com.tengan.mall.admin.infrastructure.inventory.dto;

import java.util.List;

public record StockSummaryListEnvelope(List<SkuStockSummaryEnvelope> items) {
}
