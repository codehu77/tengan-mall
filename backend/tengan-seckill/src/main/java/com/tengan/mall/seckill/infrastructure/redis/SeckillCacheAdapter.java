package com.tengan.mall.seckill.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * {@code seckill:sku:{skuId}}（String/JSON）是「這顆 SKU 現在是不是活躍秒殺」的唯一真相來源——
 * TTL 精確設在活動 end_time，key 過期就代表已結束，呼叫端（tengan-order 的訂單建立 Saga）
 * 自然落回一般商品路徑，不需要另外寫「是否過期」的判斷邏輯（見規劃第 3、4.2 節）。
 *
 * <p>原本設計有一個 randomCode 防繞過欄位，後來確認保留配額整個是服務對服務呼叫（瀏覽器碰不到
 * 任何搶購端點），要防的攻擊面根本不存在，已拿掉（見前台整合規劃 Context）。</p>
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

    /** 預熱寫入，TTL 精確設在 endTime。 */
    public void publish(Long skuId, Long activityId, java.math.BigDecimal seckillPrice, int limitPerUser,
            Instant endTime) {
        var info = new ActiveSeckillInfo(activityId, seckillPrice, limitPerUser);
        try {
            String json = objectMapper.writeValueAsString(info);
            Duration ttl = Duration.between(Instant.now(), endTime);
            if (!ttl.isNegative()) {
                redisTemplate.opsForValue().set(key(skuId), json, ttl);
            }
        } catch (Exception e) {
            throw new IllegalStateException("序列化 ActiveSeckillInfo 失敗: skuId=" + skuId, e);
        }
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
