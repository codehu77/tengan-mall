package com.tengan.mall.order.domain.repository;

import com.tengan.mall.order.domain.model.Order;
import java.util.Optional;

public interface OrderRepository {

    Optional<Order> findByOrderSn(String orderSn);

    /** 建立時 root+items 一起寫入，只有新增語意（訂單一旦建立，狀態轉換一律走下方條件式 UPDATE）。 */
    Order save(Order order);

    /** 條件式 UPDATE status=CANCELLED WHERE order_sn=? AND status=PENDING_PAYMENT，回傳是否真的搶到操作權。 */
    boolean markCancelled(String orderSn, String reason);

    /** 條件式 UPDATE status=PAID WHERE order_sn=? AND status=PENDING_PAYMENT（Phase 7 付款成功呼叫）。 */
    boolean markPaid(String orderSn);

    /** 條件式 UPDATE status=SHIPPED WHERE order_sn=? AND status=PAID。 */
    boolean markShipped(String orderSn);

    /** 條件式 UPDATE status=COMPLETED WHERE order_sn=? AND status=SHIPPED（確認收貨）。 */
    boolean markCompleted(String orderSn);
}
