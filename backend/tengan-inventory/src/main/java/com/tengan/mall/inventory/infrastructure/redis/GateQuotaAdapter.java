package com.tengan.mall.inventory.infrastructure.redis;

import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 即將開賣 Phase B 流量閘門的 Redis 原子操作，完全比照 tengan-seckill 的 QuotaGuardAdapter 寫法，
 * 但拿掉秒殺專屬的 activityId/price 概念——這裡保護的是真實倉庫存本身，不是另一份促銷配額。key
 * 用 {@code gate:} 前綴跟秒殺的 {@code seckill:} 分開命名空間，即使共用同一個 Redis 實例也不會撞。
 *
 * <p>比秒殺多一個 {@code gate:buyers:{skuId}} 集合：秒殺場次結束後限購這個概念就沒有意義了，但
 * Phase B 的限購是永久生效的（併回 member_sku_purchase_count），結算時需要知道「這次閘門到底
 * 哪些會員買過」才能逐一把 Redis 計數併回 MySQL，不能對 Redis 做 KEYS 掃描。</p>
 */
@Component
public class GateQuotaAdapter {

    private static final String STOCK_KEY_PREFIX = "gate:stock:";
    private static final String PURCHASED_KEY_PREFIX = "gate:purchased:";
    private static final String BUYERS_KEY_PREFIX = "gate:buyers:";
    private static final long ACQUIRE_TIMEOUT_MS = 100L;

    private final RedissonClient redissonClient;
    private final StringRedisTemplate redisTemplate;

    public GateQuotaAdapter(RedissonClient redissonClient, StringRedisTemplate redisTemplate) {
        this.redissonClient = redissonClient;
        this.redisTemplate = redisTemplate;
    }

    /** warm-up 用：先刪再設，強制覆蓋成絕對值（trySetPermits 對已存在的 key 是 no-op）。 */
    public void warmUp(Long skuId, int protectedStock, Instant expireAt) {
        RSemaphore semaphore = redissonClient.getSemaphore(stockKey(skuId));
        semaphore.delete();
        semaphore.trySetPermits(protectedStock);
        semaphore.expireAt(Date.from(expireAt));
    }

    /**
     * 限購計數(INCRBY)+RSemaphore 兩關都過才算保留成功，任一關失敗都要把已做的異動扣回去，
     * 過關後額外把 memberId 記進 buyers 集合供結算重播使用。
     */
    public GateReserveOutcome tryReserve(Long skuId, Long memberId, int count, int limitPerUser, Instant expireAt) {
        String purchasedKey = purchasedKey(skuId, memberId);
        long total = redisTemplate.opsForValue().increment(purchasedKey, count);
        redisTemplate.expireAt(purchasedKey, Date.from(expireAt));
        if (total > limitPerUser) {
            redisTemplate.opsForValue().increment(purchasedKey, -count);
            return GateReserveOutcome.LIMIT_EXCEEDED;
        }

        RSemaphore semaphore = redissonClient.getSemaphore(stockKey(skuId));
        boolean acquired;
        try {
            acquired = semaphore.tryAcquire(count, ACQUIRE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("搶購流量閘門配額時執行緒被中斷: skuId=" + skuId, e);
        }
        if (!acquired) {
            redisTemplate.opsForValue().increment(purchasedKey, -count);
            return GateReserveOutcome.SOLD_OUT;
        }

        redisTemplate.opsForSet().add(buyersKey(skuId), String.valueOf(memberId));
        redisTemplate.expireAt(buyersKey(skuId), Date.from(expireAt));
        return GateReserveOutcome.SUCCESS;
    }

    /** 補償動作：把 RSemaphore permits 跟限購計數都還回去（buyers 集合不用移除，結算時讀到 0 剩餘不影響正確性）。 */
    public void release(Long skuId, Long memberId, int count) {
        redissonClient.getSemaphore(stockKey(skuId)).release(count);
        redisTemplate.opsForValue().increment(purchasedKey(skuId, memberId), -count);
    }

    /** 結算排程用：protectedStock - availablePermits 即為實際賣出量。 */
    public int availablePermits(Long skuId) {
        return redissonClient.getSemaphore(stockKey(skuId)).availablePermits();
    }

    /** 結算排程用：這次閘門期間有買過的所有會員 id。 */
    public Set<Long> buyers(Long skuId) {
        Set<String> raw = redisTemplate.opsForSet().members(buyersKey(skuId));
        if (raw == null) {
            return Set.of();
        }
        return raw.stream().map(Long::valueOf).collect(java.util.stream.Collectors.toSet());
    }

    /** 結算排程用：某會員這次閘門期間實際買了幾件。 */
    public int purchasedCount(Long skuId, Long memberId) {
        String value = redisTemplate.opsForValue().get(purchasedKey(skuId, memberId));
        return value == null ? 0 : Integer.parseInt(value);
    }

    /** 結算完成後清掉 stock/buyers key，purchased 計數器靠 TTL 自然過期，不用特地刪。 */
    public void clear(Long skuId) {
        redissonClient.getSemaphore(stockKey(skuId)).delete();
        redisTemplate.delete(buyersKey(skuId));
    }

    private String stockKey(Long skuId) {
        return STOCK_KEY_PREFIX + skuId;
    }

    private String purchasedKey(Long skuId, Long memberId) {
        return PURCHASED_KEY_PREFIX + skuId + ":" + memberId;
    }

    private String buyersKey(Long skuId) {
        return BUYERS_KEY_PREFIX + skuId;
    }

    public enum GateReserveOutcome {
        SUCCESS, SOLD_OUT, LIMIT_EXCEEDED
    }
}
