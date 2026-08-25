package com.tengan.mall.product.infrastructure.persistence;

import com.tengan.mall.product.domain.repository.SkuSaleCountRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SkuSaleCountRepositoryImpl implements SkuSaleCountRepository {

    private final SkuMapper skuMapper;

    public SkuSaleCountRepositoryImpl(SkuMapper skuMapper) {
        this.skuMapper = skuMapper;
    }

    @Override
    public void increment(Long skuId, int delta) {
        skuMapper.incrementSaleCount(skuId, delta);
    }
}
