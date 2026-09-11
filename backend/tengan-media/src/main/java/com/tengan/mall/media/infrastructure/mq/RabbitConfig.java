package com.tengan.mall.media.infrastructure.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消費端（tengan-media）自己宣告 exchange/queue/binding，比照 tengan-inventory 訂閱
 * product-launch-config-exchange 的既有模式——exchange 名稱/routing key 要跟 tengan-product 那邊
 * 的 RabbitConfig（{@code product-media-usage-exchange} / {@code product.media-usage.synced}）對齊。
 * tengan-product 的事件類別在這個服務不存在，{@code TypePrecedence.INFERRED} 讓轉換器改用
 * @RabbitListener 方法簽章宣告的參數型別，這也是這個服務第一次用到 RabbitMQ。
 */
@Configuration
public class RabbitConfig {

    private static final String PRODUCT_MEDIA_USAGE_EXCHANGE = "product-media-usage-exchange";
    private static final String ROUTING_KEY_MEDIA_USAGE_SYNCED = "product.media-usage.synced";

    public static final String PRODUCT_MEDIA_USAGE_SYNCED_QUEUE = "media.product.media-usage.synced.queue";

    @Bean
    public TopicExchange productMediaUsageExchange() {
        return new TopicExchange(PRODUCT_MEDIA_USAGE_EXCHANGE);
    }

    @Bean
    public Queue productMediaUsageSyncedQueue() {
        return new Queue(PRODUCT_MEDIA_USAGE_SYNCED_QUEUE, true);
    }

    @Bean
    public Binding productMediaUsageSyncedBinding(Queue productMediaUsageSyncedQueue,
            TopicExchange productMediaUsageExchange) {
        return BindingBuilder.bind(productMediaUsageSyncedQueue).to(productMediaUsageExchange)
                .with(ROUTING_KEY_MEDIA_USAGE_SYNCED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }
}
