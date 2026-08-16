package com.tengan.mall.payment.domain.exception;

/** 訂單狀態不是 PENDING_PAYMENT（例如已取消/已逾時），不可發起付款。 */
public class OrderNotPayableException extends RuntimeException {

    public OrderNotPayableException(String orderSn) {
        super("訂單目前狀態不可付款: orderSn=" + orderSn);
    }
}
