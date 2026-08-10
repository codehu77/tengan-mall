package com.tengan.mall.order.application.order;

public interface ConfirmOrderUseCase {

    OrderConfirmResult confirm(Long memberId);
}
