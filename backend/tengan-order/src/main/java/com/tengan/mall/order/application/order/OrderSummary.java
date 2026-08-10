package com.tengan.mall.order.application.order;

import java.math.BigDecimal;
import java.time.Instant;

/** 分頁清單用的攤平投影（CQRS-lite，見 ddd-standards.md 第五節），customer/admin 共用同一個形狀。 */
public record OrderSummary(Long id, String orderSn, Long memberId, int status, BigDecimal payAmount,
        String paymentMethod, Instant createdAt) {
}
