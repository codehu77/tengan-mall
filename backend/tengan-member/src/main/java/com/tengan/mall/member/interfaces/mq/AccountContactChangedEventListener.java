package com.tengan.mall.member.interfaces.mq;

import com.tengan.mall.member.application.member.UpdateContactFromEventCommand;
import com.tengan.mall.member.application.member.UpdateContactFromEventUseCase;
import com.tengan.mall.member.infrastructure.mq.AccountContactChangedEvent;
import com.tengan.mall.member.infrastructure.mq.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AccountContactChangedEventListener {

    private final UpdateContactFromEventUseCase updateContactFromEventUseCase;

    public AccountContactChangedEventListener(UpdateContactFromEventUseCase updateContactFromEventUseCase) {
        this.updateContactFromEventUseCase = updateContactFromEventUseCase;
    }

    @RabbitListener(queues = RabbitConfig.CONTACT_CHANGED_QUEUE)
    public void onContactChanged(AccountContactChangedEvent event) {
        updateContactFromEventUseCase
                .update(new UpdateContactFromEventCommand(event.accountId(), event.phone(), event.email()));
    }
}
