package com.tengan.mall.order.infrastructure.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

/**
 * tengan-order 同時是這組佇列的生產者跟消費者（跟 product-search-exchange 那種「生產者只宣告
 * exchange、消費端各自宣告 queue」的跨服務模式不同——這裡是同一個服務用 RabbitMQ 幫自己做「延遲執行」，
 * 見規劃文件六、）。
 *
 * <p>order.delay.queue 故意不接消費者，只設 x-message-ttl + x-dead-letter-exchange/routing-key，
 * TTL 到期後 RabbitMQ 自動把訊息轉送到 order.close.queue（真正有 @RabbitListener 的佇列）。</p>
 *
 * <p>order.close.queue 的消費失敗重試用 Spring Retry 包住（這個專案第一次處理 MQ 消費失敗情境——
 * tengan-search/tengan-member 的既有消費者目前都是預設無限 requeue，但這裡的訊息關係到金流/庫存
 * 正確性，重試用盡後改丟 order.close.dlq，不無限重試洗記錄），透過 default exchange（""）+
 * routingKey=佇列名稱直接送達，不需要額外建 binding。</p>
 */
@Configuration
public class RabbitConfig {

    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";
    public static final String ROUTING_KEY_CREATED = "order.created";
    public static final String ROUTING_KEY_DELAY = "order.delay";
    public static final String ROUTING_KEY_CLOSE = "order.close";

    public static final String DELAY_QUEUE = "order.delay.queue";
    public static final String CLOSE_QUEUE = "order.close.queue";
    public static final String CLOSE_DLQ = "order.close.dlq";

    @Value("${tengan.order.unpaid-timeout-ms}")
    private long unpaidTimeoutMs;

    @Bean
    public TopicExchange orderEventExchange() {
        return new TopicExchange(ORDER_EVENT_EXCHANGE);
    }

    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE)
                .withArgument("x-message-ttl", unpaidTimeoutMs)
                .withArgument("x-dead-letter-exchange", ORDER_EVENT_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_CLOSE)
                .build();
    }

    @Bean
    public Binding orderDelayBinding(Queue orderDelayQueue, TopicExchange orderEventExchange) {
        return BindingBuilder.bind(orderDelayQueue).to(orderEventExchange).with(ROUTING_KEY_DELAY);
    }

    @Bean
    public Queue orderCloseQueue() {
        return QueueBuilder.durable(CLOSE_QUEUE).build();
    }

    @Bean
    public Binding orderCloseBinding(Queue orderCloseQueue, TopicExchange orderEventExchange) {
        return BindingBuilder.bind(orderCloseQueue).to(orderEventExchange).with(ROUTING_KEY_CLOSE);
    }

    @Bean
    public Queue orderCloseDlq() {
        return QueueBuilder.durable(CLOSE_DLQ).build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory orderCloseListenerContainerFactory(
            ConnectionFactory connectionFactory, RabbitTemplate rabbitTemplate, MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        RetryOperationsInterceptor retryInterceptor = RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(1000, 2.0, 10000)
                .recoverer(new RepublishMessageRecoverer(rabbitTemplate, "", CLOSE_DLQ))
                .build();
        factory.setAdviceChain(retryInterceptor);
        return factory;
    }
}
