package com.tengan.mall.member.interfaces.mq;

import com.tengan.mall.member.application.member.RegisterMemberFromEventCommand;
import com.tengan.mall.member.application.member.RegisterMemberFromEventUseCase;
import com.tengan.mall.member.infrastructure.mq.MemberRegisteredEvent;
import com.tengan.mall.member.infrastructure.mq.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MemberRegisteredEventListener {

    private final RegisterMemberFromEventUseCase registerMemberFromEventUseCase;

    public MemberRegisteredEventListener(RegisterMemberFromEventUseCase registerMemberFromEventUseCase) {
        this.registerMemberFromEventUseCase = registerMemberFromEventUseCase;
    }

    @RabbitListener(queues = RabbitConfig.REGISTERED_QUEUE)
    public void onRegistered(MemberRegisteredEvent event) {
        registerMemberFromEventUseCase
                .register(new RegisterMemberFromEventCommand(event.memberId(), event.username(), event.phone()));
    }
}
