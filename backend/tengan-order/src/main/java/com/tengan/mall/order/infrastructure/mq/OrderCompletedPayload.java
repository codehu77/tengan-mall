package com.tengan.mall.order.infrastructure.mq;

import com.tengan.mall.order.domain.model.OrderItem;
import java.util.List;

/** order.completed 訊息的 payload——比照 SeckillOrderPayload 直接複用 domain 的 OrderItem，
 * 消費者（tengan-product）只需要 skuId/spuId/count，其餘欄位多帶著沒關係，不用另包精簡 DTO。 */
public record OrderCompletedPayload(String orderSn, List<OrderItem> items) {
}
