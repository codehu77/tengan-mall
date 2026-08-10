package com.tengan.mall.order.domain.exception;

/** 只有 PAID 才能標記出貨。 */
public class OrderShipmentNotAllowedException extends RuntimeException {

    public OrderShipmentNotAllowedException(String orderSn) {
        super("此訂單目前狀態不允許出貨: orderSn=" + orderSn);
    }
}
