package com.tengan.mall.product.infrastructure.mq;

import com.tengan.mall.product.application.spu.ProductMediaUsageEventPublisherPort;
import java.util.List;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitProductMediaUsageEventPublisherAdapter implements ProductMediaUsageEventPublisherPort {

    private static final String OWNER_TYPE_SPU = "SPU";

    private final RabbitTemplate rabbitTemplate;

    public RabbitProductMediaUsageEventPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishSynced(Long spuId, List<String> urls) {
        rabbitTemplate.convertAndSend(RabbitConfig.PRODUCT_MEDIA_USAGE_EXCHANGE,
                RabbitConfig.ROUTING_KEY_MEDIA_USAGE_SYNCED,
                new ProductMediaUsageSyncedEvent(OWNER_TYPE_SPU, spuId, urls));
    }
}
