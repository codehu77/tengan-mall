package com.tengan.mall.inventory.application.purchaseorder;

import java.util.List;

public record CreatePurchaseOrderCommand(String operator, Long wareId, String supplierName,
        List<CreatePurchaseOrderItem> items) {
}
