package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.model.ActivityStatus;
import com.tengan.mall.seckill.domain.model.SeckillSku;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSkuRepository;
import com.tengan.mall.seckill.infrastructure.redis.QuotaGuardAdapter;
import com.tengan.mall.seckill.infrastructure.redis.SeckillCacheAdapter;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 訂閱 tengan-product 的 product.removed 事件用：商品規格真的被刪除後，seckill_sku 對應的列
 * （不管掛在哪個活動）跟著清掉。如果這個 skuId 當下屬於一個 ACTIVE（已預熱、正在搶購中）的活動，
 * 要先把 Redis 配額鎖清掉，不然商品刪除後 Redis 裡的舊配額鎖還能被搶購到——完全比照
 * {@link ReplaceProductSkusService#syncRedis} 裡「新清單沒有的 skuId 要清掉 Redis」那段既有邏輯。
 */
@Service
public class RemoveProductSkusService implements RemoveProductSkusUseCase {

    private final SeckillSkuRepository skuRepository;
    private final SeckillActivityRepository activityRepository;
    private final SeckillCacheAdapter cacheAdapter;
    private final QuotaGuardAdapter quotaGuardAdapter;

    public RemoveProductSkusService(SeckillSkuRepository skuRepository, SeckillActivityRepository activityRepository,
            SeckillCacheAdapter cacheAdapter, QuotaGuardAdapter quotaGuardAdapter) {
        this.skuRepository = skuRepository;
        this.activityRepository = activityRepository;
        this.cacheAdapter = cacheAdapter;
        this.quotaGuardAdapter = quotaGuardAdapter;
    }

    @Override
    public void remove(List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return;
        }
        List<SeckillSku> affected = skuRepository.findBySkuIds(skuIds);
        for (SeckillSku sku : affected) {
            activityRepository.findById(sku.getActivityId())
                    .filter(activity -> activity.getStatus() == ActivityStatus.ACTIVE)
                    .ifPresent(activity -> {
                        cacheAdapter.evict(sku.getSkuId());
                        quotaGuardAdapter.clear(sku.getSkuId());
                    });
        }
        skuRepository.deleteBySkuIds(skuIds);
    }
}
