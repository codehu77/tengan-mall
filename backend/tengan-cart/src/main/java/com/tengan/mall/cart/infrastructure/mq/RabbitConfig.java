package com.tengan.mall.cart.infrastructure.mq;

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
 * 這是這個服務第一個 @RabbitListener。訂閱 tengan-product 既有的 product-search-exchange /
 * product.removed（tengan-search 現在就在用的同一個 exchange/routing key，不另外開一個功能重複
 * 的事件），消費端自己宣告 exchange/queue/binding，比照 tengan-inventory 訂閱其他事件的既有模式。
 * tengan-product 的事件類別在這個服務不存在，{@code TypePrecedence.INFERRED} 讓轉換器改用
 * @RabbitListener 方法簽章宣告的參數型別。
 */
@Configuration
public class RabbitConfig {

    private static final String PRODUCT_SEARCH_EXCHANGE = "product-search-exchange";
    private static final String ROUTING_KEY_REMOVED = "product.removed";

    public static final String PRODUCT_REMOVED_QUEUE = "cart.product.removed.queue";

    @Bean
    public TopicExchange productSearchExchange() {
        return new TopicExchange(PRODUCT_SEARCH_EXCHANGE);
    }

    @Bean
    public Queue productRemovedQueue() {
        return new Queue(PRODUCT_REMOVED_QUEUE, true);
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
