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
    public void publish(Long accountId, String phone, String email, String nickname, String avatarUrl) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.MEMBER_EVENT_EXCHANGE,
                RabbitConfig.MEMBER_REGISTERED_ROUTING_KEY,
                new MemberRegisteredEvent(accountId, phone, email, nickname, avatarUrl));
    }
}
