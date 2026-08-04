package com.tengan.mall.auth.infrastructure.redis;

import com.tengan.mall.auth.application.port.SmsCodeStorePort;
import com.tengan.mall.auth.domain.exception.SmsCooldownException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 刻意不接真實簡訊商，OTP 安全設計（TTL、一次性核對、發送冷卻）照做——見
 * 微服務前台API待開發清單.md 第2節「開發細節：簡訊驗證碼」。
 */
@Component
public class RedisSmsCodeStoreAdapter implements SmsCodeStorePort {

    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Duration COOLDOWN_TTL = Duration.ofSeconds(60);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final StringRedisTemplate redisTemplate;

    public RedisSmsCodeStoreAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateAndStore(String phone, String purpose) {
        String limitKey = limitKey(phone);
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(limitKey, "1", COOLDOWN_TTL);
        if (!Boolean.TRUE.equals(acquired)) {
            throw new SmsCooldownException(phone);
        }

        String code = String.valueOf(100000 + RANDOM.nextInt(900000));
        redisTemplate.opsForValue().set(codeKey(phone, purpose), code, CODE_TTL);
        return code;
    }

    @Override
    public boolean verifyAndConsume(String phone, String purpose, String code) {
        String key = codeKey(phone, purpose);
        String stored = redisTemplate.opsForValue().get(key);
        boolean matched = Objects.equals(stored, code);
        if (matched) {
            redisTemplate.delete(key);
        }
        return matched;
    }

    @Override
    public boolean peek(String phone, String purpose, String code) {
        String stored = redisTemplate.opsForValue().get(codeKey(phone, purpose));
        return Objects.equals(stored, code);
    }

    private String codeKey(String phone, String purpose) {
        return "sms:code:%s:%s".formatted(phone, purpose);
    }

    private String limitKey(String phone) {
        return "sms:limit:%s".formatted(phone);
    }
}
