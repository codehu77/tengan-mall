package com.tengan.mall.admin.application.port;

import java.util.List;

public record ReceivePurchaseOrderPayload(List<ReceivePurchaseOrderItemPayload> items) {
}
