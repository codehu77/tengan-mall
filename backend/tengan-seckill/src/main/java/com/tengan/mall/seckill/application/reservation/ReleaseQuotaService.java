package com.tengan.mall.seckill.application.reservation;

import com.tengan.mall.seckill.infrastructure.redis.QuotaGuardAdapter;
import org.springframework.stereotype.Service;

@Service
public class ReleaseQuotaService implements ReleaseQuotaUseCase {

    private final QuotaGuardAdapter quotaGuardAdapter;

    public ReleaseQuotaService(QuotaGuardAdapter quotaGuardAdapter) {
        this.quotaGuardAdapter = quotaGuardAdapter;
    }

    @Override
    public void release(ReleaseQuotaCommand command) {
        quotaGuardAdapter.release(command.skuId(), command.memberId(), command.count());
    }
}
