package com.tengan.mall.order.application.order;

import java.util.List;

public record MyOrderPageResult(List<OrderSummary> items, long total) {
}
