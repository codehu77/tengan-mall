package com.tengan.mall.wallet.application.rule;

import com.tengan.mall.wallet.domain.repository.WalletRuleRepository;
import org.springframework.stereotype.Service;

@Service
public class GetWalletRuleService implements GetWalletRuleUseCase {

    private final WalletRuleRepository walletRuleRepository;

    public GetWalletRuleService(WalletRuleRepository walletRuleRepository) {
        this.walletRuleRepository = walletRuleRepository;
    }

    @Override
    public WalletRuleView get() {
        var rule = walletRuleRepository.get();
        return new WalletRuleView(rule.getCashbackRatePro(), rule.getCashbackRateProPlus(), rule.getMonthlyCapPro(),
                rule.getMonthlyCapProPlus(), rule.getPointExpiryDays(), rule.getGracePeriodMinutes(),
                rule.getPointValueRatio());
    }
}
