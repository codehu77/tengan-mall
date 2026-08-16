package com.tengan.mall.wallet.application.points;

import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import com.tengan.mall.wallet.domain.repository.WalletRuleRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class GetPointsSummaryService implements GetPointsSummaryUseCase {

    private static final int EXPIRING_WINDOW_DAYS = 30;

    private final PointsTransactionRepository pointsTransactionRepository;
    private final WalletRuleRepository walletRuleRepository;

    public GetPointsSummaryService(PointsTransactionRepository pointsTransactionRepository,
            WalletRuleRepository walletRuleRepository) {
        this.pointsTransactionRepository = pointsTransactionRepository;
        this.walletRuleRepository = walletRuleRepository;
    }

    @Override
    public GetPointsSummaryResult get(Long memberId) {
        int available = pointsTransactionRepository.sumConfirmedPoints(memberId);
        int pending = pointsTransactionRepository.sumPendingPoints(memberId);
        Instant cutoff = Instant.now().plusSeconds((long) EXPIRING_WINDOW_DAYS * 24 * 3600);
        int expiring = pointsTransactionRepository.findActiveEarnBatches(memberId).stream()
                .filter(tx -> tx.getExpiresAt() != null && !tx.getExpiresAt().isAfter(cutoff))
                .mapToInt(tx -> tx.getPoints()).sum();
        var ratio = walletRuleRepository.get().getPointValueRatio();
        return new GetPointsSummaryResult(available, pending, expiring, EXPIRING_WINDOW_DAYS, ratio);
    }
}
