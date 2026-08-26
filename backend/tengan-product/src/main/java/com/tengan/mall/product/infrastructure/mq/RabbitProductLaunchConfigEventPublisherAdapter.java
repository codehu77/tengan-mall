package com.tengan.mall.product.infrastructure.mq;

import com.tengan.mall.product.application.spu.ProductLaunchConfigEventPublisherPort;
import com.tengan.mall.product.application.spu.SkuLaunchConfigPayload;
import java.util.List;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitProductLaunchConfigEventPublisherAdapter implements ProductLaunchConfigEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    public RabbitProductLaunchConfigEventPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishUpserted(Long spuId, List<SkuLaunchConfigPayload> skus) {
        if (skus.isEmpty()) {
            return;
        }
        rabbitTemplate.convertAndSend(RabbitConfig.PRODUCT_LAUNCH_CONFIG_EXCHANGE,
                RabbitConfig.ROUTING_KEY_LAUNCH_CONFIG_UPSERTED, new ProductLaunchConfigUpsertedEvent(spuId, skus));
    }
}
