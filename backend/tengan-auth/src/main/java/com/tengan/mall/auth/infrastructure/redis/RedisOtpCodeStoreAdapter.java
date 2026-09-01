package com.tengan.mall.auth.infrastructure.redis;

import com.tengan.mall.auth.application.port.OtpCodeStorePort;
import com.tengan.mall.auth.domain.exception.OtpCooldownException;
import com.tengan.mall.auth.domain.exception.TooManyOtpAttemptsException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 刻意不接真實簡訊/郵件商，OTP 安全設計（TTL、一次性核對、發送冷卻、失敗次數上限）照做——見
 * 微服務前台API待開發清單.md 第2節「開發細節：簡訊驗證碼」。identifier 可以是 phone 或 email，
 * Redis key 字首沿用 sms: 不改名（跟格式無關，純歷史命名，改名沒有實質好處只會在部署當下造成
 * 殘留 key 對不上）。
 */
@Component
public class RedisOtpCodeStoreAdapter implements OtpCodeStorePort {

    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Duration COOLDOWN_TTL = Duration.ofSeconds(60);
    private static final int FAIL_LIMIT = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final StringRedisTemplate redisTemplate;

    public RedisOtpCodeStoreAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateAndStore(String identifier, String purpose) {
        String limitKey = limitKey(identifier);
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(limitKey, "1", COOLDOWN_TTL);
        if (!Boolean.TRUE.equals(acquired)) {
            throw new OtpCooldownException(identifier);
        }

        String code = String.valueOf(100000 + RANDOM.nextInt(900000));
        redisTemplate.opsForValue().set(codeKey(identifier, purpose), code, CODE_TTL);
        redisTemplate.delete(failKey(identifier, purpose));
        return code;
    }

    @Override
    public boolean verifyAndConsume(String identifier, String purpose, String code) {
        String key = codeKey(identifier, purpose);
        String stored = redisTemplate.opsForValue().get(key);
        boolean matched = Objects.equals(stored, code);
        if (matched) {
            redisTemplate.delete(key);
            redisTemplate.delete(failKey(identifier, purpose));
            return true;
        }

        String failKey = failKey(identifier, purpose);
        Long failCount = redisTemplate.opsForValue().increment(failKey);
        if (failCount != null && failCount == 1) {
            redisTemplate.expire(failKey, CODE_TTL);
        }
        if (failCount != null && failCount >= FAIL_LIMIT) {
            redisTemplate.delete(key);
            redisTemplate.delete(failKey);
            throw new TooManyOtpAttemptsException();
        }
        return false;
    }

    private String codeKey(String identifier, String purpose) {
        return "sms:code:%s:%s".formatted(identifier, purpose);
    }

    private String failKey(String identifier, String purpose) {
        return "sms:fail:%s:%s".formatted(identifier, purpose);
    }

    private String limitKey(String identifier) {
        return "sms:limit:%s".formatted(identifier);
    }
}
