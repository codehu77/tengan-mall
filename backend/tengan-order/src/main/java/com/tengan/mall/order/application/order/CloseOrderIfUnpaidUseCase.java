package com.tengan.mall.order.application.order;

public interface CloseOrderIfUnpaidUseCase {

    void close(String orderSn);
}
