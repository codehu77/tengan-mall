package com.tengan.mall.order.application.admin;

import com.tengan.mall.order.domain.exception.OrderShipmentNotAllowedException;
import com.tengan.mall.order.domain.model.OrderOperLog;
import com.tengan.mall.order.domain.repository.OrderOperLogRepository;
import com.tengan.mall.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

/** PAID→SHIPPED，客服/倉管人工登錄物流單號後觸發，稽核記 order_oper_log（見後台API文件「訂單管理」）。 */
@Service
public class ShipOrderService implements ShipOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderOperLogRepository orderOperLogRepository;

    public ShipOrderService(OrderRepository orderRepository, OrderOperLogRepository orderOperLogRepository) {
        this.orderRepository = orderRepository;
        this.orderOperLogRepository = orderOperLogRepository;
    }

    @Override
    public void ship(ShipOrderCommand command) {
        if (!orderRepository.markShipped(command.orderSn())) {
            throw new OrderShipmentNotAllowedException(command.orderSn());
        }
        orderOperLogRepository.save(
                OrderOperLog.create(command.operator(), "ship", "標記出貨 orderSn=" + command.orderSn()));
    }
}
