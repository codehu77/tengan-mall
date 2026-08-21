package com.tengan.mall.seckill.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * {@code seckill:sku:{skuId}}（String/JSON）是「這顆 SKU 現在是不是活躍秒殺」的唯一真相來源——
 * TTL 精確設在活動 end_time，key 過期就代表已結束，呼叫端（tengan-order 的訂單建立 Saga）
 * 自然落回一般商品路徑，不需要另外寫「是否過期」的判斷邏輯（見規劃第 3、4.2 節）。
 *
 * <p>{@code randomCode} 只在單品詳情端點回傳給前端，列表端點不回，防止繞過頁面直打搶購 API；
 * 內部的 RSemaphore key（見 {@link QuotaGuardAdapter}）直接用 skuId 命名，不需要靠 randomCode
 * 額外遮蔽——randomCode 的作用僅止於「前端必須先呼叫過詳情端點才拿得到」這個門檻，不是用來保護
 * 內部 Redis key 的機密性。</p>
 */
@Component
public class SeckillCacheAdapter {

    private static final String SKU_KEY_PREFIX = "seckill:sku:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public SeckillCacheAdapter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /** 預熱寫入，TTL 精確設在 endTime。回傳每個 skuId 對應產生的 randomCode，供呼叫端記錄用（目前沒有用到）。 */
    public String publish(Long skuId, Long activityId, java.math.BigDecimal seckillPrice, int limitPerUser,
            Instant endTime) {
        String randomCode = UUID.randomUUID().toString();
        var info = new ActiveSeckillInfo(activityId, seckillPrice, limitPerUser, randomCode);
        try {
            String json = objectMapper.writeValueAsString(info);
            Duration ttl = Duration.between(Instant.now(), endTime);
            if (!ttl.isNegative()) {
                redisTemplate.opsForValue().set(key(skuId), json, ttl);
            }
        } catch (Exception e) {
            throw new IllegalStateException("序列化 ActiveSeckillInfo 失敗: skuId=" + skuId, e);
        }
        return randomCode;
    }

    public Optional<ActiveSeckillInfo> lookup(Long skuId) {
        String json = redisTemplate.opsForValue().get(key(skuId));
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, ActiveSeckillInfo.class));
        } catch (Exception e) {
            throw new IllegalStateException("反序列化 ActiveSeckillInfo 失敗: skuId=" + skuId, e);
        }
    }

    public Map<Long, ActiveSeckillInfo> batchLookup(List<Long> skuIds) {
        return skuIds.stream()
                .map(skuId -> Map.entry(skuId, lookup(skuId)))
                .filter(entry -> entry.getValue().isPresent())
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
    }

    private String key(Long skuId) {
        return SKU_KEY_PREFIX + skuId;
    }
}
