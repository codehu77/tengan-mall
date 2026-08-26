package com.tengan.mall.inventory.infrastructure.mq;

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
 * 消費端（tengan-inventory）自己宣告 exchange/queue/binding，比照 tengan-search 訂閱
 * product-search-exchange 的既有模式——exchange 名稱/routing key 要跟 tengan-order 那邊的
 * RabbitConfig（{@code order-event-exchange} / {@code order.paid}）對齊。tengan-order 的事件類別
 * 在這個服務不存在，{@code TypePrecedence.INFERRED} 讓轉換器改用 @RabbitListener 方法簽章宣告的參數型別。
 */
@Configuration
public class RabbitConfig {

    private static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";
    private static final String ROUTING_KEY_PAID = "order.paid";

    public static final String ORDER_PAID_QUEUE = "inventory.order.paid.queue";

    private static final String PRODUCT_LAUNCH_CONFIG_EXCHANGE = "product-launch-config-exchange";
    private static final String ROUTING_KEY_LAUNCH_CONFIG_UPSERTED = "product.launch-config.upserted";

    public static final String PRODUCT_LAUNCH_CONFIG_QUEUE = "inventory.product.launch-config.upserted.queue";

    @Bean
    public TopicExchange orderEventExchange() {
        return new TopicExchange(ORDER_EVENT_EXCHANGE);
    }

    @Bean
    public Queue orderPaidQueue() {
        return new Queue(ORDER_PAID_QUEUE, true);
    }

    @Bean
    public Binding orderPaidBinding(Queue orderPaidQueue, TopicExchange orderEventExchange) {
        return BindingBuilder.bind(orderPaidQueue).to(orderEventExchange).with(ROUTING_KEY_PAID);
    }

    @Bean
    public TopicExchange productLaunchConfigExchange() {
        return new TopicExchange(PRODUCT_LAUNCH_CONFIG_EXCHANGE);
    }

    @Bean
    public Queue productLaunchConfigQueue() {
        return new Queue(PRODUCT_LAUNCH_CONFIG_QUEUE, true);
    }

    @Bean
    public Binding productLaunchConfigBinding(Queue productLaunchConfigQueue,
            TopicExchange productLaunchConfigExchange) {
        return BindingBuilder.bind(productLaunchConfigQueue).to(productLaunchConfigExchange)
                .with(ROUTING_KEY_LAUNCH_CONFIG_UPSERTED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }
}
