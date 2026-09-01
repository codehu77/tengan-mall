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
 * auth:refresh:{tokenId} → {accountId, familyId, used, rememberMe}（微服務前台API待開發清單.md
 * 第2節）。TTL 依 rememberMe 決定：未勾選 ≈ 1 天／勾選 ≈ 30 天，rotation 沿用原 session 的
 * TTL 類別。另外用兩個索引：
 * - auth:refresh:family:{familyId} 存這個 family 底下發過的所有 tokenId，是 revokeFamily
 *   （reuse detection 觸發）唯一能找到「要撤銷哪些 key」的方式；
 * - auth:refresh:account:{accountId} 存這個帳號名下所有 familyId，是 revokeAllForAccount
 *   （忘記密碼重設後強制全裝置登出）唯一能找到「要撤銷哪些 family」的方式。
 * Redis 沒有原生「依 value 內某欄位反查所有 key」的操作，只能自己維護這兩份索引。
 */
@Component
public class RedisRefreshTokenStoreAdapter implements RefreshTokenStorePort {

    private static final Duration REMEMBER_TTL = Duration.ofDays(30);
    private static final Duration DEFAULT_TTL = Duration.ofDays(1);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisRefreshTokenStoreAdapter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String issue(Long accountId, boolean rememberMe) {
        String familyId = UUID.randomUUID().toString();
        return issueUnderFamily(accountId, familyId, rememberMe);
    }

    @Override
    public Optional<RefreshTokenEntry> find(String tokenId) {
        String json = redisTemplate.opsForValue().get(tokenKey(tokenId));
        return Optional.ofNullable(json).map(this::deserialize);
    }

    @Override
    public String rotate(String oldTokenId, RefreshTokenEntry entry) {
        writeEntry(oldTokenId, new RefreshTokenEntry(entry.accountId(), entry.familyId(), true, entry.rememberMe()),
                ttlFor(entry.rememberMe()));
        return issueUnderFamily(entry.accountId(), entry.familyId(), entry.rememberMe());
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
    public void revokeAllForAccount(Long accountId) {
        String accountKey = accountKey(accountId);
        Set<String> familyIds = redisTemplate.opsForSet().members(accountKey);
        if (familyIds != null) {
            familyIds.forEach(this::revokeFamily);
        }
        redisTemplate.delete(accountKey);
    }

    @Override
    public void delete(String tokenId) {
        find(tokenId).ifPresent(entry -> redisTemplate.opsForSet().remove(familyKey(entry.familyId()), tokenId));
        redisTemplate.delete(tokenKey(tokenId));
    }

    @Override
    public long ttlSecondsFor(boolean rememberMe) {
        return ttlFor(rememberMe).toSeconds();
    }

    private String issueUnderFamily(Long accountId, String familyId, boolean rememberMe) {
        String tokenId = UUID.randomUUID().toString();
        Duration ttl = ttlFor(rememberMe);
        writeEntry(tokenId, new RefreshTokenEntry(accountId, familyId, false, rememberMe), ttl);
        redisTemplate.opsForSet().add(familyKey(familyId), tokenId);
        redisTemplate.expire(familyKey(familyId), ttl);
        redisTemplate.opsForSet().add(accountKey(accountId), familyId);
        redisTemplate.expire(accountKey(accountId), REMEMBER_TTL);
        return tokenId;
    }

    private void writeEntry(String tokenId, RefreshTokenEntry entry, Duration ttl) {
        redisTemplate.opsForValue().set(tokenKey(tokenId), serialize(entry), ttl);
    }

    private Duration ttlFor(boolean rememberMe) {
        return rememberMe ? REMEMBER_TTL : DEFAULT_TTL;
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

    private String accountKey(Long accountId) {
        return "auth:refresh:account:" + accountId;
    }
}
