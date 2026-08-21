package com.tengan.mall.seckill.application.reservation;

import com.tengan.mall.seckill.domain.exception.ActivityNotFoundException;
import com.tengan.mall.seckill.domain.exception.SeckillNotActiveException;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.infrastructure.redis.QuotaGuardAdapter;
import com.tengan.mall.seckill.infrastructure.redis.SeckillCacheAdapter;
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
        var activity = activityRepository.findById(info.activityId())
                .orElseThrow(() -> new ActivityNotFoundException(info.activityId()));

        quotaGuardAdapter.tryReserve(command.skuId(), command.memberId(), command.count(), info.limitPerUser(),
                activity.getEndTime());
        return new ReserveQuotaResult(info.activityId(), info.seckillPrice());
    }
}
