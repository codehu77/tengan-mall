package com.tengan.mall.cart.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tengan.mall.cart.application.cart.GuestCartItem;
import com.tengan.mall.cart.application.cart.GuestCartPort;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * key = cart:guest:{guestKey}，Hash 結構，field = skuId，value = 序列化的 GuestCartItem
 * （見 cart_storage_decision）。每次寫入刷新 TTL = 7 天——跟 tengan-auth 的
 * RedisRefreshTokenStoreAdapter 是同一種序列化/TTL 模式。
 */
@Component
public class GuestCartRedisAdapter implements GuestCartPort {

    private static final Duration TTL = Duration.ofDays(7);
    private static final String KEY_PREFIX = "cart:guest:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public GuestCartRedisAdapter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<GuestCartItem> findAll(String guestKey) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        Map<String, String> entries = ops.entries(key(guestKey));
        return entries.values().stream().map(this::deserialize).toList();
    }

    @Override
    public Optional<GuestCartItem> find(String guestKey, Long skuId) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        String json = ops.get(key(guestKey), skuId.toString());
        return Optional.ofNullable(json).map(this::deserialize);
    }

    @Override
    public void save(String guestKey, GuestCartItem item) {
        String key = key(guestKey);
        redisTemplate.opsForHash().put(key, item.skuId().toString(), serialize(item));
        redisTemplate.expire(key, TTL);
    }

    @Override
    public void remove(String guestKey, Long skuId) {
        redisTemplate.opsForHash().delete(key(guestKey), skuId.toString());
    }

    @Override
    public void removeChecked(String guestKey) {
        findAll(guestKey).stream().filter(GuestCartItem::checked)
                .forEach(item -> remove(guestKey, item.skuId()));
    }

    @Override
    public void setCheckedAll(String guestKey, boolean checked) {
        String key = key(guestKey);
        findAll(guestKey).forEach(item -> redisTemplate.opsForHash().put(key, item.skuId().toString(),
                serialize(new GuestCartItem(item.skuId(), item.count(), checked, item.specText()))));
    }

    @Override
    public void deleteAll(String guestKey) {
        redisTemplate.delete(key(guestKey));
    }

    private String key(String guestKey) {
        return KEY_PREFIX + guestKey;
    }

    private String serialize(GuestCartItem item) {
        try {
            return objectMapper.writeValueAsString(item);
        } catch (Exception e) {
            throw new IllegalStateException("序列化 GuestCartItem 失敗", e);
        }
    }

    private GuestCartItem deserialize(String json) {
        try {
            return objectMapper.readValue(json, GuestCartItem.class);
        } catch (Exception e) {
            throw new IllegalStateException("反序列化 GuestCartItem 失敗: " + json, e);
        }
    }
}
