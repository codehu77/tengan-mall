package com.tengan.mall.order.application.admin;

import com.tengan.mall.order.application.order.OrderQueryPort;
import org.springframework.stereotype.Service;

/** 供 Phase 11 dashboard 用（比照 tengan-member 的 GetMemberStatsTodayUseCase）。 */
@Service
public class GetOrderStatsTodayService implements GetOrderStatsTodayUseCase {

    private final OrderQueryPort orderQueryPort;

    public GetOrderStatsTodayService(OrderQueryPort orderQueryPort) {
        this.orderQueryPort = orderQueryPort;
    }

    @Override
    public OrderStatsTodayResult get() {
        return new OrderStatsTodayResult(orderQueryPort.countCreatedToday());
    }
}
