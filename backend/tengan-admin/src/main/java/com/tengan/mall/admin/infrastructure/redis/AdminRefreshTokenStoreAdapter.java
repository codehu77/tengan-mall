package com.tengan.mall.admin.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tengan.mall.admin.application.port.AdminRefreshTokenEntry;
import com.tengan.mall.admin.application.port.AdminRefreshTokenStorePort;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * admin:refresh:{tokenId} → {adminUserId, familyId, used}，比照 tengan-auth 的
 * RedisRefreshTokenStoreAdapter（rotation + reuse detection，family 索引用法一致）。
 */
@Component
public class AdminRefreshTokenStoreAdapter implements AdminRefreshTokenStorePort {

    private static final Duration TOKEN_TTL = Duration.ofDays(7);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public AdminRefreshTokenStoreAdapter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String issue(Long adminUserId) {
        String familyId = UUID.randomUUID().toString();
        return issueUnderFamily(adminUserId, familyId);
    }

    @Override
    public Optional<AdminRefreshTokenEntry> find(String tokenId) {
        String json = redisTemplate.opsForValue().get(tokenKey(tokenId));
        return Optional.ofNullable(json).map(this::deserialize);
    }

    @Override
    public String rotate(String oldTokenId, AdminRefreshTokenEntry entry) {
        writeEntry(oldTokenId, new AdminRefreshTokenEntry(entry.adminUserId(), entry.familyId(), true));
        return issueUnderFamily(entry.adminUserId(), entry.familyId());
    }

    @Override
    public void revokeFamily(String familyId) {
        String familyKey = familyKey(familyId);
        Set<String> tokenIds = redisTemplate.opsForSet().members(familyKey);
        if (tokenIds != null) {
            tokenIds.forEach(tokenId -> redisTemplate.delete(tokenKey(tokenId)));
        }
        redisTemplate.delete(familyKey);
    }

    @Override
    public void delete(String tokenId) {
        find(tokenId).ifPresent(entry -> redisTemplate.opsForSet().remove(familyKey(entry.familyId()), tokenId));
        redisTemplate.delete(tokenKey(tokenId));
    }

    private String issueUnderFamily(Long adminUserId, String familyId) {
        String tokenId = UUID.randomUUID().toString();
        writeEntry(tokenId, new AdminRefreshTokenEntry(adminUserId, familyId, false));
        redisTemplate.opsForSet().add(familyKey(familyId), tokenId);
        redisTemplate.expire(familyKey(familyId), TOKEN_TTL);
        return tokenId;
    }

    private void writeEntry(String tokenId, AdminRefreshTokenEntry entry) {
        redisTemplate.opsForValue().set(tokenKey(tokenId), serialize(entry), TOKEN_TTL);
    }

    private String serialize(AdminRefreshTokenEntry entry) {
        try {
            return objectMapper.writeValueAsString(entry);
        } catch (Exception e) {
            throw new IllegalStateException("序列化 AdminRefreshTokenEntry 失敗", e);
        }
    }

    private AdminRefreshTokenEntry deserialize(String json) {
        try {
            return objectMapper.readValue(json, AdminRefreshTokenEntry.class);
        } catch (Exception e) {
            throw new IllegalStateException("反序列化 AdminRefreshTokenEntry 失敗: " + json, e);
        }
    }

    private String tokenKey(String tokenId) {
        return "admin:refresh:" + tokenId;
    }

    private String familyKey(String familyId) {
        return "admin:refresh:family:" + familyId;
    }
}
