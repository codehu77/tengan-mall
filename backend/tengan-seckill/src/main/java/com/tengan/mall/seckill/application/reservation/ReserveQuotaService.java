package com.tengan.mall.seckill.application.reservation;

import com.tengan.mall.seckill.domain.exception.ActivityNotFoundException;
import com.tengan.mall.seckill.domain.exception.SeckillNotActiveException;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.infrastructure.redis.QuotaGuardAdapter;
import com.tengan.mall.seckill.infrastructure.redis.SeckillCacheAdapter;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class ReserveQuotaService implements ReserveQuotaUseCase {

    private final SeckillCacheAdapter cacheAdapter;
    private final QuotaGuardAdapter quotaGuardAdapter;
    private final SeckillActivityRepository activityRepository;

    public ReserveQuotaService(SeckillCacheAdapter cacheAdapter, QuotaGuardAdapter quotaGuardAdapter,
            SeckillActivityRepository activityRepository) {
        this.cacheAdapter = cacheAdapter;
        this.quotaGuardAdapter = quotaGuardAdapter;
        this.activityRepository = activityRepository;
    }

    @Override
    public ReserveQuotaResult reserve(ReserveQuotaCommand command) {
        var info = cacheAdapter.lookup(command.skuId())
                .orElseThrow(() -> new SeckillNotActiveException(command.skuId()));
        // 預熱排程會提前把 Redis key 寫好（配額備妥），但 key 存在不代表現在已經開賣，
        // 這裡要額外擋掉「還沒到 startTime 就想搶先用秒殺價下單」（真實 bug，見場次機制修正）。
        if (Instant.now().isBefore(info.startTime())) {
            throw new SeckillNotActiveException(command.skuId());
        }
        var activity = activityRepository.findById(info.activityId())
                .orElseThrow(() -> new ActivityNotFoundException(info.activityId()));

        quotaGuardAdapter.tryReserve(command.skuId(), command.memberId(), command.count(), info.limitPerUser(),
                activity.getEndTime());
        return new ReserveQuotaResult(info.activityId(), info.seckillPrice());
    }
}
