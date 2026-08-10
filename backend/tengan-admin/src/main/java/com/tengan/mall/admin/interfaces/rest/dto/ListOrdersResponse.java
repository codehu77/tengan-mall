package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record ListOrdersResponse(List<OrderSummaryResponse> items, long total) {
}
