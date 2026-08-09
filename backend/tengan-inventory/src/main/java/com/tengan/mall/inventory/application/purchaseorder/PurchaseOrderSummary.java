package com.tengan.mall.inventory.application.purchaseorder;

import java.time.Instant;

public record PurchaseOrderSummary(Long id, String poNumber, Long wareId, String supplierName, int status,
        Instant createdAt, Instant receivedAt) {
}
