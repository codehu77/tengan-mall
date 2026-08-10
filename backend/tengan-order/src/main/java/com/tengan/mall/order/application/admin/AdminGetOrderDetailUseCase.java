package com.tengan.mall.order.application.admin;

import com.tengan.mall.order.application.order.OrderDetailView;

public interface AdminGetOrderDetailUseCase {

    OrderDetailView get(String orderSn);
}
