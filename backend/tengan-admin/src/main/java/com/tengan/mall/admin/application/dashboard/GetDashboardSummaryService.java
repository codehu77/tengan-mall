package com.tengan.mall.admin.application.dashboard;

import com.tengan.mall.admin.application.port.InventoryStockPort;
import com.tengan.mall.admin.application.port.OrderPort;
import org.springframework.stereotype.Service;

@Service
public class GetDashboardSummaryService implements GetDashboardSummaryUseCase {

    /** 對應 tengan-order 的 OrderStatus.PAID，見該服務 OrderMapper 的狀態數字對照表註解。 */
    private static final int ORDER_STATUS_PAID = 2;
    private static final int REVENUE_TREND_DAYS = 7;

    private final OrderPort orderPort;
    private final InventoryStockPort inventoryStockPort;

    public GetDashboardSummaryService(OrderPort orderPort, InventoryStockPort inventoryStockPort) {
        this.orderPort = orderPort;
        this.inventoryStockPort = inventoryStockPort;
    }

    @Override
    public DashboardSummaryResult get() {
        var orderStats = orderPort.getTodayStats();
        long pendingShipmentCount = orderPort.listOrders(ORDER_STATUS_PAID, null, null, 1, 1).total();
        int lowStockSkuCount = inventoryStockPort.countLowStock();
        var revenueTrend = orderPort.getRevenueTrend(REVENUE_TREND_DAYS);
        return new DashboardSummaryResult(orderStats.newOrderCount(), orderStats.revenueToday(),
                orderStats.monthRevenue(), orderStats.yearRevenue(), pendingShipmentCount, lowStockSkuCount,
                revenueTrend);
    }
}
