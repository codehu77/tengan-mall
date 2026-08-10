package com.tengan.mall.order.application.order;

import org.springframework.stereotype.Service;

@Service
public class ListMyOrdersService implements ListMyOrdersUseCase {

    private final OrderQueryPort orderQueryPort;

    public ListMyOrdersService(OrderQueryPort orderQueryPort) {
        this.orderQueryPort = orderQueryPort;
    }

    @Override
    public MyOrderPageResult list(ListMyOrdersQuery query) {
        var items = orderQueryPort.search(query.memberId(), query.status(), query.pageNum(), query.pageSize());
        long total = orderQueryPort.countSearch(query.memberId(), query.status());
        return new MyOrderPageResult(items, total);
    }
}
