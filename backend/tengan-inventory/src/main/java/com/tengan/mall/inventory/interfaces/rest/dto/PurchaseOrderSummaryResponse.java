package com.tengan.mall.inventory.interfaces.rest.dto;

public record PurchaseOrderSummaryResponse(Long id, String poNumber, Long wareId, String supplierName, int status,
        String createdAt, String receivedAt) {
}
