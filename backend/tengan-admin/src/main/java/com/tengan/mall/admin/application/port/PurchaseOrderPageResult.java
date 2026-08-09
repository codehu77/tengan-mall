package com.tengan.mall.admin.application.port;

import java.util.List;

public record PurchaseOrderPageResult(List<PurchaseOrderSummary> items, long total) {
}
