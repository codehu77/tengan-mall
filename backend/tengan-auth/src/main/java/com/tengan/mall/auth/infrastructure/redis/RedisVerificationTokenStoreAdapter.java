package com.tengan.mall.auth.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tengan.mall.auth.application.port.VerificationTokenStorePort;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/** auth:vtoken:{token} → payload（JSON），registrationToken/resetToken 共用同一套一次性憑證機制。 */
@Component
public class RedisVerificationTokenStoreAdapter implements VerificationTokenStorePort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisVerificationTokenStoreAdapter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String issue(Map<String, String> payload, Duration ttl) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(key(token), serialize(payload), ttl);
        return token;
    }

    @Override
    public Optional<Map<String, String>> consume(String token) {
        String key = key(token);
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }
        redisTemplate.delete(key);
        return Optional.of(deserialize(json));
    }

    private String serialize(Map<String, String> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("序列化驗證憑證失敗", e);
        }
    }

    private Map<String, String> deserialize(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("反序列化驗證憑證失敗: " + json, e);
        }
    }

    private String key(String token) {
        return "auth:vtoken:" + token;
    }
}
