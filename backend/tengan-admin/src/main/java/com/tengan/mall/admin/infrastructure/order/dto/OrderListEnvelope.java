package com.tengan.mall.admin.infrastructure.order.dto;

import com.tengan.mall.admin.application.port.OrderSummaryItem;
import java.util.List;

public record OrderListEnvelope(List<OrderSummaryItem> items, long total) {
}
