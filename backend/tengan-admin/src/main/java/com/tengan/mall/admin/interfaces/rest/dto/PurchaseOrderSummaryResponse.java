package com.tengan.mall.admin.interfaces.rest.dto;

public record PurchaseOrderSummaryResponse(Long id, String poNumber, Long wareId, String supplierName, int status,
        String createdAt, String receivedAt) {
}
