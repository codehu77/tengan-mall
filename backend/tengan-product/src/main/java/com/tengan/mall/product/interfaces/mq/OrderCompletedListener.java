package com.tengan.mall.product.interfaces.mq;

import com.tengan.mall.product.application.spu.RecordOrderCompletedSaleCountUseCase;
import com.tengan.mall.product.application.spu.SkuCountItem;
import com.tengan.mall.product.infrastructure.mq.OrderCompletedEvent;
import com.tengan.mall.product.infrastructure.mq.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** 訂閱 tengan-order 發布的 order.completed 事件，觸發 sku.sale_count 遞增+重發 product.upserted。 */
@Component
public class OrderCompletedListener {

    private final RecordOrderCompletedSaleCountUseCase useCase;

    public OrderCompletedListener(RecordOrderCompletedSaleCountUseCase useCase) {
        this.useCase = useCase;
    }

    @RabbitListener(queues = RabbitConfig.ORDER_COMPLETED_QUEUE)
    public void onOrderCompleted(OrderCompletedEvent event) {
        var items = event.items().stream()
                .map(i -> new SkuCountItem(i.skuId(), i.spuId(), i.count()))
                .toList();
        useCase.record(event.orderSn(), items);
    }
}
