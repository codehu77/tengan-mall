package com.tengan.mall.payment.application.webhook;

public interface HandleEcpayCallbackUseCase {

    void handle(HandleEcpayCallbackCommand command);
}
