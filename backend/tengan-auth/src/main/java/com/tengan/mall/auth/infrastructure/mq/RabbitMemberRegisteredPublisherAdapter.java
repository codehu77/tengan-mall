package com.tengan.mall.auth.infrastructure.mq;

import com.tengan.mall.auth.application.port.MemberRegisteredEventPublisherPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMemberRegisteredPublisherAdapter implements MemberRegisteredEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMemberRegisteredPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(Long accountId, String username, String phone) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.MEMBER_EVENT_EXCHANGE,
                RabbitConfig.MEMBER_REGISTERED_ROUTING_KEY,
                new MemberRegisteredEvent(accountId, username, phone));
    }
}
