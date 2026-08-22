package com.tengan.mall.seckill.infrastructure.redis;

import com.tengan.mall.seckill.domain.exception.SeckillPurchaseLimitExceededException;
import com.tengan.mall.seckill.domain.exception.SeckillSoldOutException;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 三道防線裡「限購計數 + RSemaphore」那兩道的實際操作。{@code seckill:stock:{skuId}} 直接用 skuId
 * 命名（不靠 randomCode 遮蔽，見 {@link SeckillCacheAdapter} 的說明），TTL 比活動 end_time 多留一段
 * 緩衝（{@code tengan.seckill.settlement-grace-minutes}），讓結算排程來得及在 key 過期前讀到
 * 最後的 {@link #availablePermits}——這個緩衝是已知的排程間隔依賴設計，不是無限保證，見規劃第 6 節。
 */
@Component
public class QuotaGuardAdapter {

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final String PURCHASED_KEY_PREFIX = "seckill:purchased:";
    private static final long ACQUIRE_TIMEOUT_MS = 100L;

    private final RedissonClient redissonClient;
    private final StringRedisTemplate redisTemplate;

    public QuotaGuardAdapter(RedissonClient redissonClient, StringRedisTemplate redisTemplate) {
        this.redissonClient = redissonClient;
        this.redisTemplate = redisTemplate;
    }

    /** 預熱/後台編輯已上線活動配額時用——先刪再設，強制覆蓋成絕對值（`RSemaphore.trySetPermits`
     * 對已存在的 key 是no-op，只在第一次預熱時管用；後台編輯配額也會呼叫這支，用「先刪再設」繞開
     * 這個限制，讓每次呼叫都是可靠的絕對值覆蓋，不是條件式的）。刻意不做「保留已賣出數量」的差值
     * 扣抵——後台編輯配額就是覆蓋成新設定的值，這是這個專案刻意的簡化，不是遺漏。 */
    public void initSemaphore(Long skuId, int permits, Instant expireAt) {
        RSemaphore semaphore = redissonClient.getSemaphore(stockKey(skuId));
        semaphore.delete();
        semaphore.trySetPermits(permits);
        semaphore.expireAt(Date.from(expireAt));
    }

    /**
     * 限購計數（INCRBY）+ RSemaphore 兩關都過才算保留成功；任一關失敗都要把已經做的異動扣回去。
     * 呼叫端（tengan-order 的補償堆疊）只需要在保留失敗時整筆訂單失敗，成功時記一個補償動作
     * （呼叫 {@link #release}）即可，不用自己操心 Redis 細節（見規劃第 4.2 節）。
     */
    public void tryReserve(Long skuId, Long memberId, int count, int limitPerUser, Instant activityEndTime) {
        String purchasedKey = purchasedKey(skuId, memberId);
        long total = redisTemplate.opsForValue().increment(purchasedKey, count);
        redisTemplate.expireAt(purchasedKey, Date.from(activityEndTime));
        if (total > limitPerUser) {
            redisTemplate.opsForValue().increment(purchasedKey, -count);
            throw new SeckillPurchaseLimitExceededException(skuId, limitPerUser);
        }

        RSemaphore semaphore = redissonClient.getSemaphore(stockKey(skuId));
        boolean acquired;
        try {
            acquired = semaphore.tryAcquire(count, ACQUIRE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("搶購配額時執行緒被中斷: skuId=" + skuId, e);
        }
        if (!acquired) {
            redisTemplate.opsForValue().increment(purchasedKey, -count);
            throw new SeckillSoldOutException(skuId);
        }
    }

    /** 補償動作：把 RSemaphore permits 跟限購計數都還回去。 */
    public void release(Long skuId, Long memberId, int count) {
        redissonClient.getSemaphore(stockKey(skuId)).release(count);
        redisTemplate.opsForValue().increment(purchasedKey(skuId, memberId), -count);
    }

    /** 結算排程用：{@code seckillCount - availablePermits()} 即為實際賣出量。 */
    public int availablePermits(Long skuId) {
        return redissonClient.getSemaphore(stockKey(skuId)).availablePermits();
    }

    /** 商品被從已上線活動移除時清掉配額鎖，避免 Redis key 還在、讓已經被拿掉的規格繼續可以被搶購。 */
    public void clear(Long skuId) {
        redissonClient.getSemaphore(stockKey(skuId)).delete();
    }

    private String stockKey(Long skuId) {
        return STOCK_KEY_PREFIX + skuId;
    }

    private String purchasedKey(Long skuId, Long memberId) {
        return PURCHASED_KEY_PREFIX + skuId + ":" + memberId;
    }
}
