package com.tengan.mall.order.interfaces.mq;

import com.tengan.mall.order.application.order.MaterializeSeckillOrderCommand;
import com.tengan.mall.order.application.order.MaterializeSeckillOrderUseCase;
import com.tengan.mall.order.infrastructure.mq.RabbitConfig;
import com.tengan.mall.order.infrastructure.mq.SeckillOrderPayload;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SeckillOrderListener {

    private final MaterializeSeckillOrderUseCase materializeSeckillOrderUseCase;

    public SeckillOrderListener(MaterializeSeckillOrderUseCase materializeSeckillOrderUseCase) {
        this.materializeSeckillOrderUseCase = materializeSeckillOrderUseCase;
    }

    @RabbitListener(queues = RabbitConfig.SECKILL_ORDER_QUEUE,
            containerFactory = "orderSeckillOrderListenerContainerFactory")
    public void onMessage(SeckillOrderPayload payload) {
        materializeSeckillOrderUseCase.materialize(new MaterializeSeckillOrderCommand(payload.orderSn(),
                payload.memberId(), payload.paymentMethod(), payload.couponId(), payload.pointsUsed(),
                payload.pointsDiscountAmount(), payload.receiverName(), payload.receiverPhone(), payload.city(),
                payload.district(), payload.postalCode(), payload.street(), payload.remark(), payload.totalAmount(),
                payload.discountAmount(), payload.payAmount(), payload.items()));
    }
}
