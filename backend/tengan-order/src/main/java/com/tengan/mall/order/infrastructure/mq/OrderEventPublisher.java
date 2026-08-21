package com.tengan.mall.order.infrastructure.mq;

import com.tengan.mall.order.application.port.OrderEventPort;
import com.tengan.mall.order.domain.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher implements OrderEventPort {

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishOrderCreatedAndScheduleTimeout(String orderSn) {
        OrderSnEvent event = new OrderSnEvent(orderSn);
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EVENT_EXCHANGE, RabbitConfig.ROUTING_KEY_CREATED, event);
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EVENT_EXCHANGE, RabbitConfig.ROUTING_KEY_DELAY, event);
    }

    @Override
    public void publishOrderPaid(String orderSn) {
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EVENT_EXCHANGE, RabbitConfig.ROUTING_KEY_PAID,
                new OrderSnEvent(orderSn));
    }

    @Override
    public void publishSeckillOrderCreated(Order order) {
        SeckillOrderPayload payload = new SeckillOrderPayload(order.getOrderSn(), order.getMemberId(),
                order.getPaymentMethod(), order.getCouponId(), order.getPointsUsed(),
                order.getPointsDiscountAmount(), order.getReceiverName(), order.getReceiverPhone(), order.getCity(),
                order.getDistrict(), order.getPostalCode(), order.getStreet(), order.getRemark(),
                order.getTotalAmount(), order.getDiscountAmount(), order.getPayAmount(), order.getItems());
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EVENT_EXCHANGE, RabbitConfig.ROUTING_KEY_SECKILL_ORDER,
                payload);
    }
}
