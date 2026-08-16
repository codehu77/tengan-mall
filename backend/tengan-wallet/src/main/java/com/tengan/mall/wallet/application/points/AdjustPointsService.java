package com.tengan.mall.wallet.application.points;

import com.tengan.mall.wallet.domain.exception.InsufficientPointsException;
import com.tengan.mall.wallet.domain.model.PointsTransaction;
import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import org.springframework.stereotype.Service;

/** 供 tengan-admin 呼叫，對應「客服補償/扣回點數」情境，正負皆可。 */
@Service
public class AdjustPointsService implements AdjustPointsUseCase {

    private final PointsTransactionRepository pointsTransactionRepository;

    public AdjustPointsService(PointsTransactionRepository pointsTransactionRepository) {
        this.pointsTransactionRepository = pointsTransactionRepository;
    }

    @Override
    public void adjust(AdjustPointsCommand command) {
        int balance = pointsTransactionRepository.sumConfirmedPoints(command.memberId());
        if (command.points() < 0 && -command.points() > balance) {
            throw new InsufficientPointsException(command.memberId(), -command.points(), balance);
        }
        var transaction = PointsTransaction.adjust(command.memberId(), command.points(), "客服調整", command.reason(),
                command.operator());
        transaction.assignBalanceAfter(balance + command.points());
        pointsTransactionRepository.save(transaction);
    }
}
