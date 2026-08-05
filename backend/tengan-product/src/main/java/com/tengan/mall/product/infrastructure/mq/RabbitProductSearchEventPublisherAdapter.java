package com.tengan.mall.product.infrastructure.mq;

import com.tengan.mall.product.application.spu.ProductSearchEventPublisherPort;
import com.tengan.mall.product.application.spu.SkuSearchDocumentPayload;
import java.util.List;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitProductSearchEventPublisherAdapter implements ProductSearchEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    public RabbitProductSearchEventPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishUpserted(List<SkuSearchDocumentPayload> skus) {
        if (skus.isEmpty()) {
            return;
        }
        rabbitTemplate.convertAndSend(RabbitConfig.PRODUCT_SEARCH_EXCHANGE, RabbitConfig.ROUTING_KEY_UPSERTED,
                new ProductUpsertedEvent(skus));
    }

    @Override
    public void publishRemoved(Long spuId, List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return;
        }
        rabbitTemplate.convertAndSend(RabbitConfig.PRODUCT_SEARCH_EXCHANGE, RabbitConfig.ROUTING_KEY_REMOVED,
                new ProductRemovedEvent(spuId, skuIds));
    }
}
