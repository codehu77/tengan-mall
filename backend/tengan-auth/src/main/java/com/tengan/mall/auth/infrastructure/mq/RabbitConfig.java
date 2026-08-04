package com.tengan.mall.auth.infrastructure.mq;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String MEMBER_EVENT_EXCHANGE = "member-event-exchange";
    public static final String MEMBER_REGISTERED_ROUTING_KEY = "member.registered";

    /**
     * tengan-member 還沒建好，這個 exchange 目前沒有任何 queue 綁定——訊息會被丟棄，這是
     * 預期行為，不是 bug（微服務前台API待開發清單.md 第2節：tengan-auth 發完事件就不管了，
     * 消費端何時就緒是 tengan-member 自己的事）。
     */
    @Bean
    public TopicExchange memberEventExchange() {
        return new TopicExchange(MEMBER_EVENT_EXCHANGE);
    }

    /** 預設是 Java 序列化，改成 JSON 才能跨服務／跨語言消費。 */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
