package com.tengan.mall.order.infrastructure.persistence;

import com.tengan.mall.order.domain.model.OrderOperLog;
import com.tengan.mall.order.domain.repository.OrderOperLogRepository;
import java.time.ZoneId;
import org.springframework.stereotype.Repository;

@Repository
public class OrderOperLogRepositoryImpl implements OrderOperLogRepository {

    private final OrderOperLogMapper orderOperLogMapper;

    public OrderOperLogRepositoryImpl(OrderOperLogMapper orderOperLogMapper) {
        this.orderOperLogMapper = orderOperLogMapper;
    }

    @Override
    public OrderOperLog save(OrderOperLog log) {
        OrderOperLogPO po = new OrderOperLogPO();
        po.setOperator(log.getOperator());
        po.setAction(log.getAction());
        po.setTargetDesc(log.getTargetDesc());
        po.setCreatedAt(log.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime());
        orderOperLogMapper.insert(po);
        log.assignId(po.getId());
        return log;
    }
}
