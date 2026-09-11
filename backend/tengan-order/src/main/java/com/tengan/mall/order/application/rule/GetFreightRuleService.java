package com.tengan.mall.order.application.rule;

import com.tengan.mall.order.domain.repository.FreightRuleRepository;
import org.springframework.stereotype.Service;

@Service
public class GetFreightRuleService implements GetFreightRuleUseCase {

    private final FreightRuleRepository freightRuleRepository;

    public GetFreightRuleService(FreightRuleRepository freightRuleRepository) {
        this.freightRuleRepository = freightRuleRepository;
    }

    @Override
    public FreightRuleView get() {
        var rule = freightRuleRepository.get();
        return new FreightRuleView(rule.getFreeShippingThreshold(), rule.getShippingFee());
    }
}
