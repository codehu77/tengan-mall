package com.tengan.mall.payment.application.payment;

public interface InitiatePaymentUseCase {

    InitiatePaymentResult initiate(InitiatePaymentCommand command);
}
