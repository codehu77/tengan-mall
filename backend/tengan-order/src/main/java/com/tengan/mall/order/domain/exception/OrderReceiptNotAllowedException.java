package com.tengan.mall.order.domain.exception;

/** 只有 SHIPPED 才能確認收貨。 */
public class OrderReceiptNotAllowedException extends RuntimeException {

    public OrderReceiptNotAllowedException(String orderSn) {
        super("此訂單目前狀態不允許確認收貨: orderSn=" + orderSn);
    }
}
