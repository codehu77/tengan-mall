package com.tengan.mall.admin.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(long todayNewOrderCount, BigDecimal todayRevenue, BigDecimal monthRevenue,
        BigDecimal yearRevenue, long pendingShipmentCount, int lowStockSkuCount,
        List<DailyRevenuePointResponse> revenueTrend) {
}
