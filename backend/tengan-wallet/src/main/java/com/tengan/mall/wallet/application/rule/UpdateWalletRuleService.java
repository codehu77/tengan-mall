package com.tengan.mall.wallet.application.rule;

import com.tengan.mall.wallet.domain.model.WalletRule;
import com.tengan.mall.wallet.domain.repository.WalletRuleRepository;
import org.springframework.stereotype.Service;

/**
 * 供 tengan-admin 行銷管理頁呼叫。grace_period_minutes/point_expiry_days 改完立刻生效（demo 用短值），
 * 不需要重啟服務——這是這兩個欄位刻意設計成 DB 而非 Nacos 靜態設定的原因。
 */
@Service
public class UpdateWalletRuleService implements UpdateWalletRuleUseCase {

    private final WalletRuleRepository walletRuleRepository;

    public UpdateWalletRuleService(WalletRuleRepository walletRuleRepository) {
        this.walletRuleRepository = walletRuleRepository;
    }

    @Override
    public void update(UpdateWalletRuleCommand command) {
        walletRuleRepository.update(new WalletRule(command.cashbackRatePro(), command.cashbackRateProPlus(),
                command.monthlyCapPro(), command.monthlyCapProPlus(), command.pointExpiryDays(),
                command.gracePeriodMinutes(), command.pointValueRatio()));
    }
}
