package com.tengan.mall.inventory.application.gate;

import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import com.tengan.mall.inventory.infrastructure.redis.GateQuotaAdapter;
import org.springframework.stereotype.Service;

/** 供後台「流量閘門」頁面用，已預熱且尚未結算的 sku 額外帶上即時 Redis 數字。 */
@Service
public class ListGateStatusesService implements ListGateStatusesUseCase {

    private final SkuLaunchConfigRepository skuLaunchConfigRepository;
    private final GateQuotaAdapter gateQuotaAdapter;

    public ListGateStatusesService(SkuLaunchConfigRepository skuLaunchConfigRepository,
            GateQuotaAdapter gateQuotaAdapter) {
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
        this.gateQuotaAdapter = gateQuotaAdapter;
    }

    @Override
    public java.util.List<GateStatusView> list() {
        return skuLaunchConfigRepository.findAllGateEnabled().stream().map(config -> {
            boolean redisAlive = config.gateWarmedAt() != null && config.gateSettledAt() == null;
            Integer availablePermits = redisAlive ? gateQuotaAdapter.availablePermits(config.skuId()) : null;
            Integer buyersCount = redisAlive ? gateQuotaAdapter.buyers(config.skuId()).size() : null;
            return new GateStatusView(config.skuId(), config.saleStartTime(), config.gateCloseTime(),
                    config.purchaseLimitPerUser(), config.gateProtectedStock(), config.gateWarmedAt(),
                    config.gateSettledAt(), availablePermits, buyersCount);
        }).toList();
    }
}
