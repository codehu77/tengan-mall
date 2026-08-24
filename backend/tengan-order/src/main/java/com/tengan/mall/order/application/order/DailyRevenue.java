package com.tengan.mall.order.application.order;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyRevenue(LocalDate date, BigDecimal revenue) {
}
