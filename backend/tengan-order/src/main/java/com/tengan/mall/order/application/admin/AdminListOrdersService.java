package com.tengan.mall.order.application.admin;

import com.tengan.mall.order.application.order.MyOrderPageResult;
import com.tengan.mall.order.application.order.OrderQueryPort;
import org.springframework.stereotype.Service;

/** 後台視角：memberId 傳 null 給 OrderQueryPort，代表不限會員，查全部訂單。 */
@Service
public class AdminListOrdersService implements AdminListOrdersUseCase {

    private final OrderQueryPort orderQueryPort;

    public AdminListOrdersService(OrderQueryPort orderQueryPort) {
        this.orderQueryPort = orderQueryPort;
    }

    @Override
    public MyOrderPageResult list(AdminListOrdersQuery query) {
        var items = orderQueryPort.search(null, query.status(), query.pageNum(), query.pageSize());
        long total = orderQueryPort.countSearch(null, query.status());
        return new MyOrderPageResult(items, total);
    }
}
