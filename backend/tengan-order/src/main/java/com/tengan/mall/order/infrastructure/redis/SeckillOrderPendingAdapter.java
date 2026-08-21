package com.tengan.mall.order.infrastructure.redis;

import com.tengan.mall.order.application.port.SeckillOrderPendingPort;
import java.time.Duration;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * key = order:seckill:pending:{orderSn}，value = memberId，TTL 10 分鐘（比照 OrderTokenAdapter 同一種
 * 「短期標記」寫法）。這個 key 只在「還沒真正落地」這段空窗期有意義，訂單一旦寫進 DB，查詢端點會先
 * 從 DB 查到資料就不會再看這個標記，過期後留著也不影響正確性，不用主動清除。
 */
@Component
public class SeckillOrderPendingAdapter implements SeckillOrderPendingPort {

    private static final Duration TTL = Duration.ofMinutes(10);
    private static final String KEY_PREFIX = "order:seckill:pending:";

    private final StringRedisTemplate redisTemplate;

    public SeckillOrderPendingAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void markPending(String orderSn, Long memberId) {
        redisTemplate.opsForValue().set(key(orderSn), memberId.toString(), TTL);
    }

    @Override
    public Optional<Long> findPendingMemberId(String orderSn) {
        String value = redisTemplate.opsForValue().get(key(orderSn));
        return value == null ? Optional.empty() : Optional.of(Long.valueOf(value));
    }

    private String key(String orderSn) {
        return KEY_PREFIX + orderSn;
    }
}
