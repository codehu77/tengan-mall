package com.tengan.mall.order.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

/** customer(GET /api/customer/orders)/admin(GET /internal/orders) 共用同一個扁平投影形狀。 */
public record OrderSummaryResponse(Long id, String orderSn, Long memberId, int status, BigDecimal payAmount,
        String paymentMethod, Instant createdAt) {
}
