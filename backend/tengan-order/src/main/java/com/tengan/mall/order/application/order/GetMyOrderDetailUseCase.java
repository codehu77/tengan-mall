package com.tengan.mall.order.application.order;

public interface GetMyOrderDetailUseCase {

    OrderDetailView get(Long memberId, String orderSn);
}
