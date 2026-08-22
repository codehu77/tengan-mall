package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.exception.ActivityNotFoundException;
import com.tengan.mall.seckill.domain.model.ActivityStatus;
import com.tengan.mall.seckill.domain.model.SeckillActivity;
import com.tengan.mall.seckill.domain.model.SeckillSku;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSkuRepository;
import com.tengan.mall.seckill.infrastructure.redis.QuotaGuardAdapter;
import com.tengan.mall.seckill.infrastructure.redis.SeckillCacheAdapter;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 「一個活動綁多個商品，新增/編輯/刪除單一商品」用（見「設定活動商品改成列表頁」規劃）——只覆蓋
 * command 帶來的 skuIds 範圍，其餘商品的既有列不受影響，跟 {@link UpdateActivitySkusService} 的
 * 「整批覆蓋全部商品」是兩種不同語意，各自對應列表頁的「單筆存檔」跟舊版一次性表單的「整批送出」。
 * DRAFT 活動第一次設定商品要轉 PUBLISHED 的規則跟 {@link UpdateActivitySkusService} 一致。
 *
 * <p>活動如果已經是 ACTIVE（已經預熱過、正在搶購中），DB 存檔不會自動反映到 Redis——
 * {@link com.tengan.mall.seckill.application.warmup.WarmUpActivitiesService} 只找 PUBLISHED
 * 的活動，已經 ACTIVE 的活動不會再被排程撈到重新預熱，這裡要自己把異動同步進 Redis，否則使用者
 * 編輯後台配額不會反映在前台（真實 bug，見場次機制修正後使用者實測回報的問題）。價格/配額都用絕對值
 * 覆蓋（{@link QuotaGuardAdapter#initSemaphore} 這次改成先刪再設，繞開 trySetPermits 對已存在 key
 * 的 no-op 限制）；這次範圍內但新清單沒有的 skuId（商品被整個移除，或規格被拿掉）要清掉 Redis，
 * 否則舊規格還能被搶購到。</p>
 */
@Service
public class ReplaceProductSkusService implements ReplaceProductSkusUseCase {

    private final SeckillActivityRepository activityRepository;
    private final SeckillSkuRepository skuRepository;
    private final SeckillCacheAdapter cacheAdapter;
    private final QuotaGuardAdapter quotaGuardAdapter;
    private final long settlementGraceMinutes;

    public ReplaceProductSkusService(SeckillActivityRepository activityRepository,
            SeckillSkuRepository skuRepository, SeckillCacheAdapter cacheAdapter,
            QuotaGuardAdapter quotaGuardAdapter,
            @Value("${tengan.seckill.settlement-grace-minutes:60}") long settlementGraceMinutes) {
        this.activityRepository = activityRepository;
        this.skuRepository = skuRepository;
        this.cacheAdapter = cacheAdapter;
        this.quotaGuardAdapter = quotaGuardAdapter;
        this.settlementGraceMinutes = settlementGraceMinutes;
    }

    @Override
    public void replace(ReplaceProductSkusCommand command) {
        SeckillActivity activity = activityRepository.findById(command.activityId())
                .orElseThrow(() -> new ActivityNotFoundException(command.activityId()));

        var newSkus = command.items().stream()
                .map(item -> SeckillSku.create(activity.getId(), item.skuId(), item.seckillPrice(),
                        item.seckillCount(), item.limitPerUser()))
                .toList();
        skuRepository.replaceForActivityAndSkuIds(activity.getId(), command.skuIds(), newSkus);

        if (activity.getStatus() == ActivityStatus.DRAFT) {
            activity.publish();
            activityRepository.update(activity);
        } else if (activity.getStatus() == ActivityStatus.ACTIVE) {
            syncRedis(activity, command.skuIds(), newSkus);
        }
    }

    private void syncRedis(SeckillActivity activity, List<Long> skuIdsScope, List<SeckillSku> newSkus) {
        Instant semaphoreExpireAt = activity.getEndTime().plus(Duration.ofMinutes(settlementGraceMinutes));
        Set<Long> newSkuIds = newSkus.stream().map(SeckillSku::getSkuId).collect(Collectors.toSet());

        for (SeckillSku sku : newSkus) {
            cacheAdapter.publish(sku.getSkuId(), activity.getId(), sku.getSeckillPrice(), sku.getLimitPerUser(),
                    activity.getStartTime(), activity.getEndTime());
            quotaGuardAdapter.initSemaphore(sku.getSkuId(), sku.getSeckillCount(), semaphoreExpireAt);
        }

        for (Long skuId : skuIdsScope) {
            if (!newSkuIds.contains(skuId)) {
                cacheAdapter.evict(skuId);
                quotaGuardAdapter.clear(skuId);
            }
        }
    }
}
