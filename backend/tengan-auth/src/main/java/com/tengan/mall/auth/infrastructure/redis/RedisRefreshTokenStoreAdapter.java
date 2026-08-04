package com.tengan.mall.auth.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tengan.mall.auth.application.port.RefreshTokenEntry;
import com.tengan.mall.auth.application.port.RefreshTokenStorePort;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * auth:refresh:{tokenId} → {accountId, familyId, used}（微服務前台API待開發清單.md 第2節）。
 * 另外用 auth:refresh:family:{familyId} 存這個 family 底下發過的所有 tokenId，
 * 是 revokeFamily（reuse detection 觸發）唯一能找到「要撤銷哪些 key」的方式——
 * Redis 沒有原生「依 value 內某欄位反查所有 key」的操作，只能自己維護這份索引。
 */
@Component
public class RedisRefreshTokenStoreAdapter implements RefreshTokenStorePort {

    private static final Duration TOKEN_TTL = Duration.ofDays(7);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisRefreshTokenStoreAdapter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String issue(Long accountId) {
        String familyId = UUID.randomUUID().toString();
        return issueUnderFamily(accountId, familyId);
    }

    @Override
    public Optional<RefreshTokenEntry> find(String tokenId) {
        String json = redisTemplate.opsForValue().get(tokenKey(tokenId));
        return Optional.ofNullable(json).map(this::deserialize);
    }

    @Override
    public String rotate(String oldTokenId, RefreshTokenEntry entry) {
        writeEntry(oldTokenId, new RefreshTokenEntry(entry.accountId(), entry.familyId(), true));
        return issueUnderFamily(entry.accountId(), entry.familyId());
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

    private String issueUnderFamily(Long accountId, String familyId) {
        String tokenId = UUID.randomUUID().toString();
        writeEntry(tokenId, new RefreshTokenEntry(accountId, familyId, false));
        redisTemplate.opsForSet().add(familyKey(familyId), tokenId);
        redisTemplate.expire(familyKey(familyId), TOKEN_TTL);
        return tokenId;
    }

    private void writeEntry(String tokenId, RefreshTokenEntry entry) {
        redisTemplate.opsForValue().set(tokenKey(tokenId), serialize(entry), TOKEN_TTL);
    }

    private String serialize(RefreshTokenEntry entry) {
        try {
            return objectMapper.writeValueAsString(entry);
        } catch (Exception e) {
            throw new IllegalStateException("序列化 RefreshTokenEntry 失敗", e);
        }
    }

    private RefreshTokenEntry deserialize(String json) {
        try {
            return objectMapper.readValue(json, RefreshTokenEntry.class);
        } catch (Exception e) {
            throw new IllegalStateException("反序列化 RefreshTokenEntry 失敗: " + json, e);
        }
    }

    private String tokenKey(String tokenId) {
        return "auth:refresh:" + tokenId;
    }

    private String familyKey(String familyId) {
        return "auth:refresh:family:" + familyId;
    }
}
