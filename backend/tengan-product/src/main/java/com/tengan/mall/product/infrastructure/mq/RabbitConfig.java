package com.tengan.mall.product.infrastructure.mq;

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
 * tengan-search 這個消費端從一開始就存在（跟 Phase 1 tengan-auth 發 member.registered 時消費端
 * 還沒建好的情境不同），但發布端一樣只宣告 exchange，不管有沒有 queue 綁定——queue/binding 由消費端
 * （tengan-search）自己宣告，這是標準 RabbitMQ 慣例，不是本服務要操心的事。
 *
 * <p>這個服務同時也是 order.completed 的消費端（銷量地基規劃）——比照 tengan-inventory 訂閱
 * order.paid 的既有模式，消費端自己宣告 exchange/queue/binding，exchange 名稱/routing key 要跟
 * tengan-order 那邊的 RabbitConfig（{@code order-event-exchange} / {@code order.completed}）對齊。
 * tengan-order 的事件類別在這個服務不存在，{@code TypePrecedence.INFERRED} 讓轉換器改用
 * @RabbitListener 方法簽章宣告的參數型別，這也是這個服務第一個 @RabbitListener，所以要把
 * jsonMessageConverter 從單純的 Jackson2JsonMessageConverter 換成有設定 INFERRED 的版本
 * ——這個改動只影響入站反序列化（fromMessage），不影響既有的 product.upserted/removed 發布邏輯。</p>
 */
@Configuration
public class RabbitConfig {

    public static final String PRODUCT_SEARCH_EXCHANGE = "product-search-exchange";
    public static final String ROUTING_KEY_UPSERTED = "product.upserted";
    public static final String ROUTING_KEY_REMOVED = "product.removed";

    /** tengan-inventory 消費，見 ProductLaunchConfigEventPublisherPort 的說明。 */
    public static final String PRODUCT_LAUNCH_CONFIG_EXCHANGE = "product-launch-config-exchange";
    public static final String ROUTING_KEY_LAUNCH_CONFIG_UPSERTED = "product.launch-config.upserted";
    public static final String ROUTING_KEY_LAUNCH_CONFIG_REMOVED = "product.launch-config.removed";

    /** tengan-media 消費，見 ProductMediaUsageEventPublisherPort 的說明。 */
    public static final String PRODUCT_MEDIA_USAGE_EXCHANGE = "product-media-usage-exchange";
    public static final String ROUTING_KEY_MEDIA_USAGE_SYNCED = "product.media-usage.synced";

    private static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";
    private static final String ROUTING_KEY_ORDER_COMPLETED = "order.completed";
    public static final String ORDER_COMPLETED_QUEUE = "product.order.completed.queue";

    @Bean
    public TopicExchange productSearchExchange() {
        return new TopicExchange(PRODUCT_SEARCH_EXCHANGE);
    }

    /** 只宣告 exchange，不管有沒有 queue 綁定——queue/binding 由消費端（tengan-inventory）自己宣告。 */
    @Bean
    public TopicExchange productLaunchConfigExchange() {
        return new TopicExchange(PRODUCT_LAUNCH_CONFIG_EXCHANGE);
    }

    /** 只宣告 exchange，不管有沒有 queue 綁定——queue/binding 由消費端（tengan-media）自己宣告。 */
    @Bean
    public TopicExchange productMediaUsageExchange() {
        return new TopicExchange(PRODUCT_MEDIA_USAGE_EXCHANGE);
    }

    @Bean
    public TopicExchange orderEventExchange() {
        return new TopicExchange(ORDER_EVENT_EXCHANGE);
    }

    @Bean
    public Queue orderCompletedQueue() {
        return new Queue(ORDER_COMPLETED_QUEUE, true);
    }

    @Bean
    public Binding orderCompletedBinding(Queue orderCompletedQueue, TopicExchange orderEventExchange) {
        return BindingBuilder.bind(orderCompletedQueue).to(orderEventExchange).with(ROUTING_KEY_ORDER_COMPLETED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }
}
