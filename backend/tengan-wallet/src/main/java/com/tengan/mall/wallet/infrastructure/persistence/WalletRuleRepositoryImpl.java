package com.tengan.mall.wallet.infrastructure.persistence;

import com.tengan.mall.wallet.domain.model.WalletRule;
import com.tengan.mall.wallet.domain.repository.WalletRuleRepository;
import org.springframework.stereotype.Repository;

@Repository
public class WalletRuleRepositoryImpl implements WalletRuleRepository {

    private static final long SINGLETON_ID = 1L;

    private final WalletRuleMapper mapper;

    public WalletRuleRepositoryImpl(WalletRuleMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public WalletRule get() {
        WalletRulePO po = mapper.selectById(SINGLETON_ID);
        return new WalletRule(po.getCashbackRatePro(), po.getCashbackRateProPlus(), po.getMonthlyCapPro(),
                po.getMonthlyCapProPlus(), po.getPointExpiryDays(), po.getGracePeriodMinutes(), po.getPointValueRatio());
    }

    @Override
    public void update(WalletRule rule) {
        WalletRulePO po = new WalletRulePO();
        po.setId(SINGLETON_ID);
        po.setCashbackRatePro(rule.getCashbackRatePro());
        po.setCashbackRateProPlus(rule.getCashbackRateProPlus());
        po.setMonthlyCapPro(rule.getMonthlyCapPro());
        po.setMonthlyCapProPlus(rule.getMonthlyCapProPlus());
        po.setPointExpiryDays(rule.getPointExpiryDays());
        po.setGracePeriodMinutes(rule.getGracePeriodMinutes());
        po.setPointValueRatio(rule.getPointValueRatio());
        mapper.updateById(po);
    }
}
