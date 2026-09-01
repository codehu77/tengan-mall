package com.tengan.mall.auth.infrastructure.mq;

import com.tengan.mall.auth.application.port.AccountContactChangedEventPublisherPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitAccountContactChangedPublisherAdapter implements AccountContactChangedEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    public RabbitAccountContactChangedPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(Long accountId, String phone, String email) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.MEMBER_EVENT_EXCHANGE,
                RabbitConfig.MEMBER_CONTACT_CHANGED_ROUTING_KEY,
                new AccountContactChangedEvent(accountId, phone, email));
    }
}
