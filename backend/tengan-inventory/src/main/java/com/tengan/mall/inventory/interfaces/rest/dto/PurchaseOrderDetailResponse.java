package com.tengan.mall.inventory.interfaces.rest.dto;

import java.util.List;

public record PurchaseOrderDetailResponse(Long id, String poNumber, Long wareId, String supplierName, int status,
        String createdBy, String createdAt, String receivedAt, List<PurchaseOrderItemResponse> items) {
}
