package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.dashboard.GetDashboardSummaryUseCase;
import com.tengan.mall.admin.interfaces.rest.dto.DailyRevenuePointResponse;
import com.tengan.mall.admin.interfaces.rest.dto.DashboardSummaryResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 後台首頁彙總卡片，任何登入的後台使用者都看得到，比照 {@link AdminAuthController} 的 /me、/menus 不額外掛 RBAC 權限碼。 */
@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final GetDashboardSummaryUseCase getDashboardSummaryUseCase;

    public DashboardController(GetDashboardSummaryUseCase getDashboardSummaryUseCase) {
        this.getDashboardSummaryUseCase = getDashboardSummaryUseCase;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse summary() {
        var result = getDashboardSummaryUseCase.get();
        var revenueTrend = result.revenueTrend().stream()
                .map(p -> new DailyRevenuePointResponse(p.date(), p.revenue()))
                .toList();
        return new DashboardSummaryResponse(result.todayNewOrderCount(), result.todayRevenue(),
                result.monthRevenue(), result.yearRevenue(), result.pendingShipmentCount(), result.lowStockSkuCount(),
                revenueTrend);
    }
}
