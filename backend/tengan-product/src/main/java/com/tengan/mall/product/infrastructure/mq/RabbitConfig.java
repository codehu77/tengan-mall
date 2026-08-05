package com.tengan.mall.product.infrastructure.mq;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * tengan-search 這個消費端從一開始就存在（跟 Phase 1 tengan-auth 發 member.registered 時消費端
 * 還沒建好的情境不同），但發布端一樣只宣告 exchange，不管有沒有 queue 綁定——queue/binding 由消費端
 * （tengan-search）自己宣告，這是標準 RabbitMQ 慣例，不是本服務要操心的事。
 */
@Configuration
public class RabbitConfig {

    public static final String PRODUCT_SEARCH_EXCHANGE = "product-search-exchange";
    public static final String ROUTING_KEY_UPSERTED = "product.upserted";
    public static final String ROUTING_KEY_REMOVED = "product.removed";

    @Bean
    public TopicExchange productSearchExchange() {
        return new TopicExchange(PRODUCT_SEARCH_EXCHANGE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
