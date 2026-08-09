package com.tengan.mall.admin.application.port;

public record PurchaseOrderSummary(Long id, String poNumber, Long wareId, String supplierName, int status,
        String createdAt, String receivedAt) {
}
