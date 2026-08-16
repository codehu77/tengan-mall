package com.tengan.mall.wallet.application.tier;

import com.tengan.mall.wallet.domain.model.MemberTierLevel;
import com.tengan.mall.wallet.domain.repository.MemberTierRepository;
import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import com.tengan.mall.wallet.domain.repository.WalletRuleRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Service;

@Service
public class GetMemberTierService implements GetMemberTierUseCase {

    private final MemberTierRepository memberTierRepository;
    private final WalletRuleRepository walletRuleRepository;
    private final PointsTransactionRepository pointsTransactionRepository;

    public GetMemberTierService(MemberTierRepository memberTierRepository, WalletRuleRepository walletRuleRepository,
            PointsTransactionRepository pointsTransactionRepository) {
        this.memberTierRepository = memberTierRepository;
        this.walletRuleRepository = walletRuleRepository;
        this.pointsTransactionRepository = pointsTransactionRepository;
    }

    @Override
    public GetMemberTierResult get(Long memberId) {
        MemberTierLevel tier = memberTierRepository.findByMemberId(memberId).map(t -> t.getTier())
                .orElse(MemberTierLevel.FREE);
        var rule = walletRuleRepository.get();
        Instant monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        int monthlyEarned = pointsTransactionRepository.sumConfirmedEarnPointsSince(memberId, monthStart);
        return new GetMemberTierResult(tier.name(), TierLabels.labelOf(tier), rule.cashbackRateFor(tier),
                rule.monthlyCapFor(tier), monthlyEarned);
    }
}
