package com.tengan.mall.auth.infrastructure.redis;

import com.tengan.mall.auth.application.port.LoginAttemptLimiterPort;
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/** auth:login:fail:{identifier} 計數器，仿照 OTP 冷卻/失敗計數的既有模式，防登入端點暴力破解。 */
@Component
public class RedisLoginAttemptLimiterAdapter implements LoginAttemptLimiterPort {

    private static final int FAIL_LIMIT = 5;
    private static final Duration LOCKOUT_TTL = Duration.ofMinutes(15);

    private final StringRedisTemplate redisTemplate;

    public RedisLoginAttemptLimiterAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isLocked(String identifier) {
        String value = redisTemplate.opsForValue().get(key(identifier));
        return value != null && Integer.parseInt(value) >= FAIL_LIMIT;
    }

    @Override
    public void recordFailure(String identifier) {
        String key = key(identifier);
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, LOCKOUT_TTL);
        }
    }

    @Override
    public void reset(String identifier) {
        redisTemplate.delete(key(identifier));
    }

    private String key(String identifier) {
        return "auth:login:fail:%s".formatted(identifier);
    }
}
