package com.tengan.mall.wallet.application.points;

import com.tengan.mall.wallet.domain.model.PointsTransaction;
import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class ListPointsTransactionsService implements ListPointsTransactionsUseCase {

    private final PointsTransactionRepository pointsTransactionRepository;

    public ListPointsTransactionsService(PointsTransactionRepository pointsTransactionRepository) {
        this.pointsTransactionRepository = pointsTransactionRepository;
    }

    @Override
    public PointsTransactionPageResult list(ListPointsTransactionsQuery query) {
        var items = pointsTransactionRepository
                .search(query.memberId(), query.type(), query.status(), query.fromDate(), query.keyword(),
                        query.pageNum(), query.pageSize())
                .stream().map(this::toView).toList();
        long total = pointsTransactionRepository.countSearch(query.memberId(), query.type(), query.status(),
                query.fromDate(), query.keyword());
        return new PointsTransactionPageResult(items, total);
    }

    private PointsTransactionView toView(PointsTransaction tx) {
        return new PointsTransactionView(tx.getId(), tx.getType().name(), tx.getStatus().name(), tx.getPoints(),
                tx.getBalanceAfter(), tx.getTitle(), tx.getDescription(), tx.getOrderSn(), tx.getChannel(),
                tx.getOperator(), tx.getCreatedAt(), tx.getExpiresAt());
    }
}
