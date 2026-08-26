package com.tengan.mall.inventory.application.gate;

import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class GetGateStatusService implements GetGateStatusUseCase {

    private final SkuLaunchConfigRepository skuLaunchConfigRepository;

    public GetGateStatusService(SkuLaunchConfigRepository skuLaunchConfigRepository) {
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
    }

    @Override
    public GateConfigView get(Long skuId) {
        return skuLaunchConfigRepository.findBySkuId(skuId)
                .map(c -> new GateConfigView(c.skuId(), true, c.trafficGateEnabled(), c.saleStartTime(),
                        c.gateCloseTime(), c.gateWarmedAt(), c.gateSettledAt()))
                .orElseGet(() -> new GateConfigView(skuId, false, false, null, null, null, null));
    }
}
