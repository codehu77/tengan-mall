package com.tengan.mall.member.infrastructure.mq;

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
 * 消費端（tengan-member）宣告 queue + binding，exchange 名稱/routing key 要跟 tengan-auth 那邊的
 * RabbitConfig（MEMBER_EVENT_EXCHANGE="member-event-exchange"）對齊。
 *
 * <p>TypePrecedence 設 INFERRED 的理由跟 tengan-search 消費 tengan-product 事件時一樣——
 * tengan-auth 的 MemberRegisteredEvent 類別在這個服務根本不存在，讓 @RabbitListener 方法簽章
 * 宣告的參數型別接手，兩邊只靠 JSON 欄位名稱對齊，不共用型別（見 MemberRegisteredEvent 的註解）。</p>
 */
@Configuration
public class RabbitConfig {

    private static final String MEMBER_EVENT_EXCHANGE = "member-event-exchange";
    private static final String ROUTING_KEY_REGISTERED = "member.registered";

    public static final String REGISTERED_QUEUE = "member.registered.queue";

    @Bean
    public TopicExchange memberEventExchange() {
        return new TopicExchange(MEMBER_EVENT_EXCHANGE);
    }

    @Bean
    public Queue memberRegisteredQueue() {
        return new Queue(REGISTERED_QUEUE, true);
    }

    @Bean
    public Binding memberRegisteredBinding(Queue memberRegisteredQueue, TopicExchange memberEventExchange) {
        return BindingBuilder.bind(memberRegisteredQueue).to(memberEventExchange).with(ROUTING_KEY_REGISTERED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }
}
