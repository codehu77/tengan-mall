package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;

public record OrderTodayStats(long newOrderCount, BigDecimal revenueToday, BigDecimal monthRevenue,
        BigDecimal yearRevenue) {
}
