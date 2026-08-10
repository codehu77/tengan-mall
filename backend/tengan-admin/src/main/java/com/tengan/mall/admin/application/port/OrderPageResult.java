package com.tengan.mall.admin.application.port;

import java.util.List;

public record OrderPageResult(List<OrderSummaryItem> items, long total) {
}
