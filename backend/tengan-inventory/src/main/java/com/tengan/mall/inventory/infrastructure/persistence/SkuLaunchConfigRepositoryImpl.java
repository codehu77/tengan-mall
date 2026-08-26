package com.tengan.mall.inventory.infrastructure.persistence;

import com.tengan.mall.inventory.domain.model.SkuLaunchConfig;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SkuLaunchConfigRepositoryImpl implements SkuLaunchConfigRepository {

    private final SkuLaunchConfigMapper mapper;

    public SkuLaunchConfigRepositoryImpl(SkuLaunchConfigMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<SkuLaunchConfig> findBySkuId(Long skuId) {
        SkuLaunchConfigPO po = mapper.selectById(skuId);
        return po == null ? Optional.empty() : Optional.of(toDomain(po));
    }

    @Override
    public void upsert(Long skuId, LocalDateTime saleStartTime, Integer purchaseLimitPerUser) {
        mapper.upsert(skuId, saleStartTime, purchaseLimitPerUser);
    }

    @Override
    public void configureGate(Long skuId, boolean trafficGateEnabled, LocalDateTime gateCloseTime) {
        mapper.configureGate(skuId, trafficGateEnabled, gateCloseTime);
    }

    @Override
    public List<SkuLaunchConfig> findReadyToWarmUp(LocalDateTime now, LocalDateTime horizon) {
        return mapper.findReadyToWarmUp(now, horizon).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean markWarmed(Long skuId, int protectedStock, LocalDateTime warmedAt) {
        return mapper.markWarmed(skuId, protectedStock, warmedAt) > 0;
    }

    @Override
    public List<SkuLaunchConfig> findReadyToSettle(LocalDateTime now) {
        return mapper.findReadyToSettle(now).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean markSettled(Long skuId, LocalDateTime settledAt) {
        return mapper.markSettled(skuId, settledAt) > 0;
    }

    @Override
    public List<SkuLaunchConfig> findAllGateEnabled() {
        return mapper.findAllGateEnabled().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteBySkuIds(List<Long> skuIds) {
        mapper.deleteBySkuIds(skuIds);
    }

    private SkuLaunchConfig toDomain(SkuLaunchConfigPO po) {
        return new SkuLaunchConfig(po.getSkuId(), po.getSaleStartTime(), po.isTrafficGateEnabled(),
                po.getGateCloseTime(), po.getPurchaseLimitPerUser(), po.getGateProtectedStock(),
                po.getGateWarmedAt(), po.getGateSettledAt());
    }
}
