package com.tengan.mall.wallet.application.points;

import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetExpiringBatchesService implements GetExpiringBatchesUseCase {

    private final PointsTransactionRepository pointsTransactionRepository;

    public GetExpiringBatchesService(PointsTransactionRepository pointsTransactionRepository) {
        this.pointsTransactionRepository = pointsTransactionRepository;
    }

    @Override
    public List<PointBatchView> get(Long memberId) {
        return pointsTransactionRepository.findActiveEarnBatches(memberId).stream()
                .map(tx -> new PointBatchView(tx.getId(), tx.getPoints(), tx.getCreatedAt(), tx.getExpiresAt(),
                        tx.getOrderSn()))
                .toList();
    }
}
