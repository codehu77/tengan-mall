package com.tengan.mall.order.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyRevenuePointResponse(LocalDate date, BigDecimal revenue) {
}
