package com.tengan.mall.admin.infrastructure.inventory.dto;

import com.tengan.mall.admin.application.port.PurchaseOrderSummary;
import java.util.List;

public record PurchaseOrderListEnvelope(List<PurchaseOrderSummary> items, long total) {
}
