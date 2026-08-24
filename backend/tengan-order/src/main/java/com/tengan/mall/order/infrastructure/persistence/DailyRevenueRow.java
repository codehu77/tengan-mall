package com.tengan.mall.order.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;

/** MyBatis 原生 SQL 投影用的可變 POJO，比照經典 MyBatis 慣例用 getter/setter 讓自動映射生效。 */
public class DailyRevenueRow {

    private LocalDate day;
    private BigDecimal revenue;

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
