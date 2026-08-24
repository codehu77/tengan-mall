package com.tengan.mall.order.interfaces.rest.dto;

import java.math.BigDecimal;

public record OrderStatsTodayResponse(long newOrderCount, BigDecimal revenueToday, BigDecimal monthRevenue,
        BigDecimal yearRevenue) {
}
