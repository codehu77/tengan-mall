package com.tengan.mall.inventory.application.purchaseorder;

import java.util.List;

public record ListPurchaseOrdersResult(List<PurchaseOrderSummary> items, long total) {
}
