package com.tengan.mall.inventory.interfaces.rest.dto;

import java.util.List;

public record ListPurchaseOrdersResponse(List<PurchaseOrderSummaryResponse> items, long total) {
}
