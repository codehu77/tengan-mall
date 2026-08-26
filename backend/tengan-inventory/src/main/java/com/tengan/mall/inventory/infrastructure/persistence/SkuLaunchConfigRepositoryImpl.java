package com.tengan.mall.inventory.infrastructure.persistence;

import com.tengan.mall.inventory.domain.model.SkuLaunchConfig;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import java.time.LocalDateTime;
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
        if (po == null) {
            return Optional.empty();
        }
        return Optional
                .of(new SkuLaunchConfig(po.getSkuId(), po.getSaleStartTime(), po.getPurchaseLimitPerUser()));
    }

    @Override
    public void upsert(Long skuId, LocalDateTime saleStartTime, Integer purchaseLimitPerUser) {
        mapper.upsert(skuId, saleStartTime, purchaseLimitPerUser);
    }
}
