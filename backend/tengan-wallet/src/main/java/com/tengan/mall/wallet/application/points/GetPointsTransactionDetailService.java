package com.tengan.mall.wallet.application.points;

import com.tengan.mall.wallet.domain.exception.PointsTransactionNotFoundException;
import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class GetPointsTransactionDetailService implements GetPointsTransactionDetailUseCase {

    private final PointsTransactionRepository pointsTransactionRepository;

    public GetPointsTransactionDetailService(PointsTransactionRepository pointsTransactionRepository) {
        this.pointsTransactionRepository = pointsTransactionRepository;
    }

    @Override
    public PointsTransactionView get(Long memberId, Long id) {
        var tx = pointsTransactionRepository.findById(id)
                .filter(t -> t.getMemberId().equals(memberId))
                .orElseThrow(() -> new PointsTransactionNotFoundException(id));
        return new PointsTransactionView(tx.getId(), tx.getType().name(), tx.getStatus().name(), tx.getPoints(),
                tx.getBalanceAfter(), tx.getTitle(), tx.getDescription(), tx.getOrderSn(), tx.getChannel(),
                tx.getOperator(), tx.getCreatedAt(), tx.getExpiresAt());
    }
}
