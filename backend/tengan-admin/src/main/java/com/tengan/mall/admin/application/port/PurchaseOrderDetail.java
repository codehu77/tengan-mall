package com.tengan.mall.admin.application.port;

import java.util.List;

public record PurchaseOrderDetail(Long id, String poNumber, Long wareId, String supplierName, int status,
        String createdBy, String createdAt, String receivedAt, List<PurchaseOrderItem> items) {
}
