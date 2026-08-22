package com.tengan.mall.seckill.application.reservation;

import com.tengan.mall.seckill.infrastructure.redis.SeckillCacheAdapter;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CheckActiveSkusService implements CheckActiveSkusUseCase {

    private final SeckillCacheAdapter cacheAdapter;

    public CheckActiveSkusService(SeckillCacheAdapter cacheAdapter) {
        this.cacheAdapter = cacheAdapter;
    }

    @Override
    public List<ActiveSkuStatus> check(List<Long> skuIds) {
        Instant now = Instant.now();
        return cacheAdapter.batchLookup(skuIds).entrySet().stream()
                // 預熱排程會提前把 key 寫好，還沒到 startTime 就不算「已開賣」——tengan-order 據此
                // 判斷購物車項目要不要走秒殺路徑，這裡濾掉還沒開賣的，讓它落回一般商品路徑（見 ReserveQuotaService 同樣的修正）。
                .filter(entry -> !now.isBefore(entry.getValue().startTime()))
                .map(entry -> new ActiveSkuStatus(entry.getKey(), entry.getValue().activityId(),
                        entry.getValue().seckillPrice(), entry.getValue().limitPerUser()))
                .toList();
    }
}
