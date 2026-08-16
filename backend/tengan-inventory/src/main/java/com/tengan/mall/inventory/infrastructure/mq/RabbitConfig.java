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
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }
}
