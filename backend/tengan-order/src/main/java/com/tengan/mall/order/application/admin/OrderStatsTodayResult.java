package com.tengan.mall.order.application.admin;

import java.math.BigDecimal;

public record OrderStatsTodayResult(long newOrderCount, BigDecimal revenueToday, BigDecimal monthRevenue,
        BigDecimal yearRevenue) {
}
