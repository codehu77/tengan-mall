package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record ListPurchaseOrdersResponse(List<PurchaseOrderSummaryResponse> items, long total) {
}
