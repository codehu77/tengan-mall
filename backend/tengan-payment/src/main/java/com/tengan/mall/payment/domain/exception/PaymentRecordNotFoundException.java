package com.tengan.mall.payment.domain.exception;

public class PaymentRecordNotFoundException extends RuntimeException {

    public PaymentRecordNotFoundException(String orderSn) {
        super("找不到訂單的付款記錄: orderSn=" + orderSn);
    }
}
