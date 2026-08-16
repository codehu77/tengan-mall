package com.tengan.mall.payment.domain.exception;

public class PaymentAlreadyPaidException extends RuntimeException {

    public PaymentAlreadyPaidException(String orderSn) {
        super("訂單已經付款完成，不可重複發起付款: orderSn=" + orderSn);
    }
}
