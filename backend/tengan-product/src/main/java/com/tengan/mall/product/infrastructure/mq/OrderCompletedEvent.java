package com.tengan.mall.product.infrastructure.mq;

import java.util.List;

/** tengan-order 發布的 order.completed 事件（消費端自己的形狀，不共用生產端的 class）。 */
public record OrderCompletedEvent(String orderSn, List<OrderItemEvent> items) {
}
