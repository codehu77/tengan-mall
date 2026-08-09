package com.tengan.mall.inventory.application.purchaseorder;

import java.time.Instant;
import java.util.List;

public record PurchaseOrderDetailResult(Long id, String poNumber, Long wareId, String supplierName, int status,
        String createdBy, Instant createdAt, Instant receivedAt, List<PurchaseOrderItemResult> items) {
}
