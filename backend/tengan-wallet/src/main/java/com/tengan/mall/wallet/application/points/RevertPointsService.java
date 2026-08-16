package com.tengan.mall.wallet.application.points;

import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import org.springframework.stereotype.Service;

/** 只有 status=CONFIRMED AND order_sn=? 的 REDEEM 列才允許撤銷，不符合條件（沒用過點數/已撤銷過）一律 no-op。 */
@Service
public class RevertPointsService implements RevertPointsUseCase {

    private final PointsTransactionRepository pointsTransactionRepository;

    public RevertPointsService(PointsTransactionRepository pointsTransactionRepository) {
        this.pointsTransactionRepository = pointsTransactionRepository;
    }

    @Override
    public void revert(RevertPointsCommand command) {
        pointsTransactionRepository.findConfirmedRedeemByOrderSn(command.memberId(), command.orderSn())
                .ifPresent(tx -> pointsTransactionRepository.revertRedeem(tx.getId(), command.orderSn()));
    }
}
