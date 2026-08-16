package com.tengan.mall.inventory.interfaces.mq;

import com.tengan.mall.inventory.application.stock.DeductInventoryUseCase;
import com.tengan.mall.inventory.infrastructure.mq.OrderSnEvent;
import com.tengan.mall.inventory.infrastructure.mq.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** 訂閱 tengan-order 發布的 order.paid 事件，服務內直接呼叫既有 DeductInventoryUseCase（已冪等，見 Phase 5）。 */
@Component
public class OrderPaidListener {

    private final DeductInventoryUseCase deductInventoryUseCase;

    public OrderPaidListener(DeductInventoryUseCase deductInventoryUseCase) {
        this.deductInventoryUseCase = deductInventoryUseCase;
    }

    @RabbitListener(queues = RabbitConfig.ORDER_PAID_QUEUE)
    public void onOrderPaid(OrderSnEvent event) {
        deductInventoryUseCase.deduct(event.orderSn());
    }
}
