package com.tengan.mall.order.application.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** 分頁清單用的攤平投影（CQRS-lite，見 ddd-standards.md 第五節），customer/admin 共用同一個形狀。
 * items 是訂單頁要顯示的商品快照（見 OrderItem 的說明），用批次 IN 查詢帶出，不逐筆查詢。 */
public record OrderSummary(Long id, String orderSn, Long memberId, int status, BigDecimal payAmount,
        String paymentMethod, Instant createdAt, List<OrderItemView> items) {
}
