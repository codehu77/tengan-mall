package com.tengan.mall.admin.application.dashboard;

import com.tengan.mall.admin.application.port.RevenueTrendPoint;
import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResult(long todayNewOrderCount, BigDecimal todayRevenue, BigDecimal monthRevenue,
        BigDecimal yearRevenue, long pendingShipmentCount, int lowStockSkuCount,
        List<RevenueTrendPoint> revenueTrend) {
}
