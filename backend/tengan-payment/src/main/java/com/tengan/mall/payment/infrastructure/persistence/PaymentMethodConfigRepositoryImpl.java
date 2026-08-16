package com.tengan.mall.payment.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.payment.domain.model.PaymentMethodConfig;
import com.tengan.mall.payment.domain.repository.PaymentMethodConfigRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentMethodConfigRepositoryImpl implements PaymentMethodConfigRepository {

    private final PaymentMethodConfigMapper mapper;

    public PaymentMethodConfigRepositoryImpl(PaymentMethodConfigMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<PaymentMethodConfig> findAll() {
        return mapper.selectList(null).stream()
                .map(po -> new PaymentMethodConfig(po.getMethod(), Boolean.TRUE.equals(po.getEnabled()))).toList();
    }

    @Override
    public List<PaymentMethodConfig> findEnabled() {
        return mapper.selectList(new LambdaQueryWrapper<PaymentMethodConfigPO>()
                .eq(PaymentMethodConfigPO::getEnabled, true)).stream()
                .map(po -> new PaymentMethodConfig(po.getMethod(), true)).toList();
    }

    @Override
    public boolean updateEnabled(String method, boolean enabled) {
        return mapper.updateEnabled(method, enabled) > 0;
    }
}
