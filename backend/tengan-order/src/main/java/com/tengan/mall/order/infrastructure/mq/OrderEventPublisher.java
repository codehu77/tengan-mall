package com.tengan.mall.order.infrastructure.mq;

import com.tengan.mall.order.application.port.OrderEventPort;
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
}
