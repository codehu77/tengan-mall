package com.tengan.mall.inventory.application.gate;

import com.tengan.mall.inventory.domain.model.SkuLaunchConfig;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class GetGateStatusService implements GetGateStatusUseCase {

    private final SkuLaunchConfigRepository skuLaunchConfigRepository;

    public GetGateStatusService(SkuLaunchConfigRepository skuLaunchConfigRepository) {
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
    }

    @Override
    public List<GateConfigView> getBySpuIds(List<Long> spuIds) {
        if (spuIds.isEmpty()) {
            return List.of();
        }
        Map<Long, List<SkuLaunchConfig>> bySpuId = skuLaunchConfigRepository.findAllBySpuIds(spuIds).stream()
                .collect(Collectors.groupingBy(SkuLaunchConfig::spuId));
        return bySpuId.entrySet().stream()
                .map(entry -> {
                    SkuLaunchConfig representative = entry.getValue().stream()
                            .max(Comparator.comparing(SkuLaunchConfig::gateWarmedAt,
                                    Comparator.nullsFirst(Comparator.naturalOrder())))
                            .orElseThrow();
                    return new GateConfigView(entry.getKey(), representative.gateWarmedAt(),
                            representative.gateCloseTime());
                })
                .toList();
    }
}
