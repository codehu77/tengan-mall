package com.tengan.mall.order.application.rule;

import com.tengan.mall.order.domain.model.FreightRule;
import com.tengan.mall.order.domain.repository.FreightRuleRepository;
import org.springframework.stereotype.Service;

/** 供 tengan-admin 呼叫。改完立刻生效，不需要重啟服務（比照 wallet_rule 的既有體驗）。 */
@Service
public class UpdateFreightRuleService implements UpdateFreightRuleUseCase {

    private final FreightRuleRepository freightRuleRepository;

    public UpdateFreightRuleService(FreightRuleRepository freightRuleRepository) {
        this.freightRuleRepository = freightRuleRepository;
    }

    @Override
    public void update(UpdateFreightRuleCommand command) {
        freightRuleRepository.update(new FreightRule(command.freeShippingThreshold(), command.shippingFee()));
    }
}
