package com.tengan.mall.order.infrastructure.redis;

import com.tengan.mall.order.application.port.OrderTokenPort;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * key = order:token:{memberId}，TTL 10 分鐘。consume() 用 Lua script 做「比對並刪除」的單一原子操作
 * ——這不是分散式鎖，是靠 Redis 單執行緒模型保證這段邏輯不會被其他命令插隊，比鎖更輕量（見規劃文件一、5.）。
 * 如果拆成 GET+DEL 兩次呼叫，兩個併發下單請求可能都讀到「尚未被刪除」的 token，導致同一個 token
 * 被判定成功兩次。
 */
@Component
public class OrderTokenAdapter implements OrderTokenPort {

    private static final Duration TTL = Duration.ofMinutes(10);
    private static final String KEY_PREFIX = "order:token:";

    private static final DefaultRedisScript<Long> CONSUME_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then "
                    + "return redis.call('del', KEYS[1]) "
                    + "else return 0 end",
            Long.class);

    private final StringRedisTemplate redisTemplate;

    public OrderTokenAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String issue(Long memberId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(key(memberId), token, TTL);
        return token;
    }

    @Override
    public boolean consume(Long memberId, String token) {
        Long result = redisTemplate.execute(CONSUME_SCRIPT, List.of(key(memberId)), token);
        return result != null && result > 0;
    }

    private String key(Long memberId) {
        return KEY_PREFIX + memberId;
    }
}
