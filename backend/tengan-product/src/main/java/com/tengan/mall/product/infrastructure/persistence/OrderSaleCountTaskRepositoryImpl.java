package com.tengan.mall.product.infrastructure.persistence;

import com.tengan.mall.product.domain.repository.OrderSaleCountTaskRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OrderSaleCountTaskRepositoryImpl implements OrderSaleCountTaskRepository {

    private final SkuSaleCountTaskMapper mapper;

    public OrderSaleCountTaskRepositoryImpl(SkuSaleCountTaskMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean tryClaim(String orderSn) {
        return mapper.tryClaim(orderSn) > 0;
    }
}
