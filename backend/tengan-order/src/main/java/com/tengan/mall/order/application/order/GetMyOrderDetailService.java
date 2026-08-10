package com.tengan.mall.order.application.order;

import com.tengan.mall.order.domain.exception.OrderAccessDeniedException;
import com.tengan.mall.order.domain.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

/** 不洩漏「訂單存在但不是你的」——不屬於自己的訂單一律當 404 處理（見 OrderAccessDeniedException）。 */
@Service
public class GetMyOrderDetailService implements GetMyOrderDetailUseCase {

    private final OrderQueryPort orderQueryPort;

    public GetMyOrderDetailService(OrderQueryPort orderQueryPort) {
        this.orderQueryPort = orderQueryPort;
    }

    @Override
    public OrderDetailView get(Long memberId, String orderSn) {
        var detail = orderQueryPort.findDetailByOrderSn(orderSn).orElseThrow(() -> new OrderNotFoundException(orderSn));
        if (!detail.memberId().equals(memberId)) {
            throw new OrderAccessDeniedException(orderSn);
        }
        return detail;
    }
}
