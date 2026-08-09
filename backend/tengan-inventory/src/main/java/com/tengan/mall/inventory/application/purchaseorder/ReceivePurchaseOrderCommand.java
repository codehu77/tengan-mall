package com.tengan.mall.inventory.application.purchaseorder;

import java.util.List;

public record ReceivePurchaseOrderCommand(String operator, Long poId, List<ReceivePurchaseOrderItem> items) {
}
