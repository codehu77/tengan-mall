package com.tengan.mall.payment.application.port;

public interface OrderPort {

    OrderDetailForPayment getOrder(String orderSn);

    /** 呼叫 tengan-order 的 PUT /internal/orders/{orderSn}/paid，成功回傳 true；訂單已是 PAID 時 tengan-order 端會 no-op 仍回 200。 */
    void markPaid(String orderSn);
}
