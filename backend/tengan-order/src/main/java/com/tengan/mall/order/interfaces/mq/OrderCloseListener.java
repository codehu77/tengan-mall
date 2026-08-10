package com.tengan.mall.order.interfaces.mq;

import com.tengan.mall.order.application.order.CloseOrderIfUnpaidUseCase;
import com.tengan.mall.order.infrastructure.mq.OrderSnEvent;
import com.tengan.mall.order.infrastructure.mq.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCloseListener {

    private final CloseOrderIfUnpaidUseCase closeOrderIfUnpaidUseCase;

    public OrderCloseListener(CloseOrderIfUnpaidUseCase closeOrderIfUnpaidUseCase) {
        this.closeOrderIfUnpaidUseCase = closeOrderIfUnpaidUseCase;
    }

    @RabbitListener(queues = RabbitConfig.CLOSE_QUEUE, containerFactory = "orderCloseListenerContainerFactory")
    public void onClose(OrderSnEvent event) {
        closeOrderIfUnpaidUseCase.close(event.orderSn());
    }
}
