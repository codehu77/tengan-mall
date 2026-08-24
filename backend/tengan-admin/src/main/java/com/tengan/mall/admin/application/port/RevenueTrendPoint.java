package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RevenueTrendPoint(LocalDate date, BigDecimal revenue) {
}
