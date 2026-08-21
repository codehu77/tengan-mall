package com.tengan.mall.seckill.application.reservation;

import com.tengan.mall.seckill.infrastructure.redis.SeckillCacheAdapter;
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
        return cacheAdapter.batchLookup(skuIds).entrySet().stream()
                .map(entry -> new ActiveSkuStatus(entry.getKey(), entry.getValue().activityId(),
                        entry.getValue().seckillPrice(), entry.getValue().limitPerUser()))
                .toList();
    }
}
