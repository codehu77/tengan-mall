package com.tengan.mall.order.application.order;

public interface ListMyOrdersUseCase {

    MyOrderPageResult list(ListMyOrdersQuery query);
}
