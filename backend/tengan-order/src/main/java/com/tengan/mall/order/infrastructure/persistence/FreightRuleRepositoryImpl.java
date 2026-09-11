package com.tengan.mall.order.infrastructure.persistence;

import com.tengan.mall.order.domain.model.FreightRule;
import com.tengan.mall.order.domain.repository.FreightRuleRepository;
import org.springframework.stereotype.Repository;

@Repository
public class FreightRuleRepositoryImpl implements FreightRuleRepository {

    private static final long SINGLETON_ID = 1L;

    private final FreightRuleMapper mapper;

    public FreightRuleRepositoryImpl(FreightRuleMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public FreightRule get() {
        FreightRulePO po = mapper.selectById(SINGLETON_ID);
        return new FreightRule(po.getFreeShippingThreshold(), po.getShippingFee());
    }

    @Override
    public void update(FreightRule rule) {
        FreightRulePO po = new FreightRulePO();
        po.setId(SINGLETON_ID);
        po.setFreeShippingThreshold(rule.getFreeShippingThreshold());
        po.setShippingFee(rule.getShippingFee());
        mapper.updateById(po);
    }
}
