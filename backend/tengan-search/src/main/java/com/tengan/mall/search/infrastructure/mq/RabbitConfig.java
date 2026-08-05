package com.tengan.mall.search.infrastructure.mq;

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
 * 消費端（tengan-search）宣告 queue + binding，這是標準 RabbitMQ 慣例——exchange 名稱/routing key
 * 要跟 tengan-product 那邊的 RabbitConfig 對齊。upserted/removed 兩種事件形狀不同，各自綁一條 queue，
 * 不共用同一支 @RabbitListener 方法。
 *
 * <p>{@code Jackson2JsonMessageConverter} 預設用訊息裡的 {@code __TypeId__} header（發布端的完整
 * class name）反查目標型別，但 tengan-product 的事件類別在 tengan-search 這邊根本不存在，會直接
 * ClassNotFoundException。設成 {@code TypePrecedence.INFERRED} 讓它改用 {@code @RabbitListener}
 * 方法簽章宣告的參數型別，兩邊只靠 JSON 欄位名稱對齊，不共用型別（見 SkuUpsertPayload 註解）。</p>
 */
@Configuration
public class RabbitConfig {

    private static final String PRODUCT_SEARCH_EXCHANGE = "product-search-exchange";
    private static final String ROUTING_KEY_UPSERTED = "product.upserted";
    private static final String ROUTING_KEY_REMOVED = "product.removed";

    public static final String UPSERTED_QUEUE = "search.product.upserted.queue";
    public static final String REMOVED_QUEUE = "search.product.removed.queue";

    @Bean
    public TopicExchange productSearchExchange() {
        return new TopicExchange(PRODUCT_SEARCH_EXCHANGE);
    }

    @Bean
    public Queue productUpsertedQueue() {
        return new Queue(UPSERTED_QUEUE, true);
    }

    @Bean
    public Queue productRemovedQueue() {
        return new Queue(REMOVED_QUEUE, true);
    }

    @Bean
    public Binding productUpsertedBinding(Queue productUpsertedQueue, TopicExchange productSearchExchange) {
        return BindingBuilder.bind(productUpsertedQueue).to(productSearchExchange).with(ROUTING_KEY_UPSERTED);
    }

    @Bean
    public Binding productRemovedBinding(Queue productRemovedQueue, TopicExchange productSearchExchange) {
        return BindingBuilder.bind(productRemovedQueue).to(productSearchExchange).with(ROUTING_KEY_REMOVED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }
}
