package com.tengan.mall.payment.application.payment;

public interface GetPaymentStatusUseCase {

    PaymentStatusView getStatus(String orderSn);
}
