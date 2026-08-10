package com.tengan.mall.order.application.order;

public interface CreateOrderUseCase {

    CreateOrderResult create(CreateOrderCommand command);
}
