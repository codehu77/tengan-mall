package com.tengan.mall.order.interfaces.rest.dto;

import java.util.List;

public record OrderListResponse(List<OrderSummaryResponse> items, long total) {
}
