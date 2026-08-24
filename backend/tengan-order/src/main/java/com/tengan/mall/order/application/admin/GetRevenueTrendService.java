package com.tengan.mall.order.application.admin;

import com.tengan.mall.order.application.order.DailyRevenue;
import com.tengan.mall.order.application.order.OrderQueryPort;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetRevenueTrendService implements GetRevenueTrendUseCase {

    private final OrderQueryPort orderQueryPort;

    public GetRevenueTrendService(OrderQueryPort orderQueryPort) {
        this.orderQueryPort = orderQueryPort;
    }

    @Override
    public List<DailyRevenue> get(int days) {
        return orderQueryPort.revenueTrend(days);
    }
}
