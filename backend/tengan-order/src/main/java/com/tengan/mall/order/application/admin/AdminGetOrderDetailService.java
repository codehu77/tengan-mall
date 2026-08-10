package com.tengan.mall.order.application.admin;

import com.tengan.mall.order.application.order.OrderDetailView;
import com.tengan.mall.order.application.order.OrderQueryPort;
import com.tengan.mall.order.domain.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

/** 後台視角：不做歸屬檢查（客服本來就能看任何人的訂單），跟顧客自己的 GetMyOrderDetailService 分開。 */
@Service
public class AdminGetOrderDetailService implements AdminGetOrderDetailUseCase {

    private final OrderQueryPort orderQueryPort;

    public AdminGetOrderDetailService(OrderQueryPort orderQueryPort) {
        this.orderQueryPort = orderQueryPort;
    }

    @Override
    public OrderDetailView get(String orderSn) {
        return orderQueryPort.findDetailByOrderSn(orderSn).orElseThrow(() -> new OrderNotFoundException(orderSn));
    }
}
