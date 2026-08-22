package com.tengan.mall.seckill.application.warmup;

import com.tengan.mall.seckill.domain.model.SeckillActivity;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSkuRepository;
import com.tengan.mall.seckill.infrastructure.redis.QuotaGuardAdapter;
import com.tengan.mall.seckill.infrastructure.redis.SeckillCacheAdapter;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 讀 MySQL（status=PUBLISHED 且即將開始的活動）→ 寫 Redis（{@code seckill:sku:*} + RSemaphore），
 * 完成後把活動狀態轉成 ACTIVE，避免下一次排程重複預熱（見規劃第 3 節）。排程觸發時機（每日固定
 * 四個時間點）在 {@link com.tengan.mall.seckill.infrastructure.scheduler.WarmUpScheduler}，
 * 這裡只負責「找候選 + 寫 Redis」，horizon 抓 warmup-horizon-hours 是為了讓連續兩次排程之間
 * 不會漏掉「這次還沒開始、下次排程前就會開始」的活動。
 */
@Service
public class WarmUpActivitiesService implements WarmUpActivitiesUseCase {

    private final SeckillActivityRepository activityRepository;
    private final SeckillSkuRepository skuRepository;
    private final SeckillCacheAdapter cacheAdapter;
    private final QuotaGuardAdapter quotaGuardAdapter;
    private final long warmUpHorizonHours;
    private final long settlementGraceMinutes;

    public WarmUpActivitiesService(SeckillActivityRepository activityRepository, SeckillSkuRepository skuRepository,
            SeckillCacheAdapter cacheAdapter, QuotaGuardAdapter quotaGuardAdapter,
            @Value("${tengan.seckill.warmup-horizon-hours:6}") long warmUpHorizonHours,
            @Value("${tengan.seckill.settlement-grace-minutes:60}") long settlementGraceMinutes) {
        this.activityRepository = activityRepository;
        this.skuRepository = skuRepository;
        this.cacheAdapter = cacheAdapter;
        this.quotaGuardAdapter = quotaGuardAdapter;
        this.warmUpHorizonHours = warmUpHorizonHours;
        this.settlementGraceMinutes = settlementGraceMinutes;
    }

    @Override
    @Transactional
    public int warmUp() {
        Instant now = Instant.now();
        Instant horizon = now.plus(Duration.ofHours(warmUpHorizonHours));
        var candidates = activityRepository.findReadyToWarmUp(now, horizon);

        for (SeckillActivity activity : candidates) {
            var skus = skuRepository.findByActivityId(activity.getId());
            Instant semaphoreExpireAt = activity.getEndTime().plus(Duration.ofMinutes(settlementGraceMinutes));
            for (var sku : skus) {
                cacheAdapter.publish(sku.getSkuId(), activity.getId(), sku.getSeckillPrice(), sku.getLimitPerUser(),
                        activity.getStartTime(), activity.getEndTime());
                quotaGuardAdapter.initSemaphore(sku.getSkuId(), sku.getSeckillCount(), semaphoreExpireAt);
            }
            activity.activate();
            activityRepository.update(activity);
        }
        return candidates.size();
    }
}
