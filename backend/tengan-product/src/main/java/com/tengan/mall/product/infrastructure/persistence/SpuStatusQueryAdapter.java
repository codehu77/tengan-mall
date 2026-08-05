package com.tengan.mall.product.infrastructure.persistence;

import com.tengan.mall.product.application.spu.SpuStatusPort;
import com.tengan.mall.product.domain.model.SpuStatus;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SpuStatusQueryAdapter implements SpuStatusPort {

    private final SpuMapper spuMapper;

    public SpuStatusQueryAdapter(SpuMapper spuMapper) {
        this.spuMapper = spuMapper;
    }

    @Override
    public Optional<SpuStatus> findStatus(Long spuId) {
        SpuPO po = spuMapper.selectById(spuId);
        return Optional.ofNullable(po).map(SpuPO::getStatus);
    }
}
