package com.tengan.mall.order.domain.repository;

import com.tengan.mall.order.domain.model.OrderOperLog;

public interface OrderOperLogRepository {

    OrderOperLog save(OrderOperLog log);
}
