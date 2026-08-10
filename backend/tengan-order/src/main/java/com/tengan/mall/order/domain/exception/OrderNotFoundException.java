package com.tengan.mall.order.domain.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String orderSn) {
        super("訂單不存在: orderSn=" + orderSn);
    }
}
