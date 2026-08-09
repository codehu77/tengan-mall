package com.tengan.mall.admin.application.port;

import java.util.List;

public record CreatePurchaseOrderPayload(Long wareId, String supplierName,
        List<CreatePurchaseOrderItemPayload> items) {
}
