package com.tengan.mall.wallet.application.points;

import com.tengan.mall.wallet.domain.exception.InsufficientPointsException;
import com.tengan.mall.wallet.domain.model.PointsTransaction;
import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import com.tengan.mall.wallet.domain.repository.WalletRuleRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 供 tengan-order 下單 Saga 呼叫。冪等以 orderSn 為鍵——同一筆已核銷過直接回傳原折抵金額（重試視為成功）。 */
@Service
public class ConsumePointsService implements ConsumePointsUseCase {

    private final PointsTransactionRepository pointsTransactionRepository;
    private final WalletRuleRepository walletRuleRepository;

    public ConsumePointsService(PointsTransactionRepository pointsTransactionRepository,
            WalletRuleRepository walletRuleRepository) {
        this.pointsTransactionRepository = pointsTransactionRepository;
        this.walletRuleRepository = walletRuleRepository;
    }

    @Override
    @Transactional
    public ConsumePointsResult consume(ConsumePointsCommand command) {
        var existing = pointsTransactionRepository.findConfirmedRedeemByOrderSn(command.memberId(),
                command.orderSn());
        var ratio = walletRuleRepository.get().getPointValueRatio();
        if (existing.isPresent()) {
            return new ConsumePointsResult(ratio.multiply(BigDecimal.valueOf(command.points())));
        }

        int balance = pointsTransactionRepository.sumConfirmedPoints(command.memberId());
        if (command.points() > balance) {
            throw new InsufficientPointsException(command.memberId(), command.points(), balance);
        }
        var discountAmount = ratio.multiply(BigDecimal.valueOf(command.points()));
        var transaction = PointsTransaction.redeem(command.memberId(), command.points(), command.orderSn(), "結帳折抵",
                "訂單 " + command.orderSn() + " 使用點數折抵");
        transaction.assignBalanceAfter(balance - command.points());
        pointsTransactionRepository.save(transaction);
        return new ConsumePointsResult(discountAmount);
    }
}
