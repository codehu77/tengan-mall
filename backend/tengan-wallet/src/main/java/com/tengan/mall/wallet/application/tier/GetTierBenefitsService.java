package com.tengan.mall.wallet.application.tier;

import com.tengan.mall.wallet.domain.model.MemberTierLevel;
import com.tengan.mall.wallet.domain.model.WalletRule;
import com.tengan.mall.wallet.domain.repository.WalletRuleRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

/** 公開端點，不帶會員身份——isCurrent/highlight 由前端拿到這份比較表後，自己跟 tier 端點的結果比對標記。 */
@Service
public class GetTierBenefitsService implements GetTierBenefitsUseCase {

    private final WalletRuleRepository walletRuleRepository;

    public GetTierBenefitsService(WalletRuleRepository walletRuleRepository) {
        this.walletRuleRepository = walletRuleRepository;
    }

    @Override
    public List<TierBenefitView> get() {
        WalletRule rule = walletRuleRepository.get();
        return List.of(
                new TierBenefitView("FREE", TierLabels.labelOf(MemberTierLevel.FREE), "無消費回饋", "—",
                        List.of("基本會員權益", "生日禮券")),
                new TierBenefitView("PRO", TierLabels.labelOf(MemberTierLevel.PRO), rateLabel(rule.getCashbackRatePro()),
                        capLabel(rule.getMonthlyCapPro()),
                        List.of("消費回饋 " + rateLabel(rule.getCashbackRatePro()), "生日禮券", "專屬客服")),
                new TierBenefitView("PRO_PLUS", TierLabels.labelOf(MemberTierLevel.PRO_PLUS),
                        rateLabel(rule.getCashbackRateProPlus()), capLabel(rule.getMonthlyCapProPlus()),
                        List.of("消費回饋 " + rateLabel(rule.getCashbackRateProPlus()), "生日禮券", "專屬客服",
                                "回饋無上限")));
    }

    private String rateLabel(BigDecimal rate) {
        return rate.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%";
    }

    private String capLabel(Integer cap) {
        return cap == null ? "無上限" : "每月上限 " + cap + " 點";
    }
}
