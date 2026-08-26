package com.tengan.mall.seckill.interfaces.mq;

import com.tengan.mall.seckill.application.activity.RemoveProductSkusUseCase;
import com.tengan.mall.seckill.infrastructure.mq.ProductRemovedEvent;
import com.tengan.mall.seckill.infrastructure.mq.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** 訂閱 tengan-product 發布的 product.removed 事件，清掉失效 skuId 在秒殺這邊的殘留參照（含 Redis 配額鎖）。 */
@Component
public class ProductRemovedListener {

    private final RemoveProductSkusUseCase removeProductSkusUseCase;

    public ProductRemovedListener(RemoveProductSkusUseCase removeProductSkusUseCase) {
        this.removeProductSkusUseCase = removeProductSkusUseCase;
    }

    @RabbitListener(queues = RabbitConfig.PRODUCT_REMOVED_QUEUE)
    public void onProductRemoved(ProductRemovedEvent event) {
        removeProductSkusUseCase.remove(event.skuIds());
    }
}
