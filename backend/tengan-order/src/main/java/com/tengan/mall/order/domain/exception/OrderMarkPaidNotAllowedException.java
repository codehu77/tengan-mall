package com.tengan.mall.order.domain.exception;

/**
 * 訂單不是 PENDING_PAYMENT 且不是 PAID（例如已被 TTL 逾時機制轉成 CANCELLED）——付款成功通知
 * 跟逾時取消理論上可能競速（見 Phase 7 規劃「已知邊界情況」），tengan-payment 收到這個例外對應的
 * 409 後仍會把自己的 payment_record 標記 PAID（畢竟真的收到錢了），不做自動退款，改記 WARN 留人工介入。
 */
public class OrderMarkPaidNotAllowedException extends RuntimeException {

    public OrderMarkPaidNotAllowedException(String orderSn) {
        super("此訂單目前狀態不允許標記為已付款: orderSn=" + orderSn);
    }
}
