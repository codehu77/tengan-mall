package com.tengan.mall.wallet.application.points;

import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import com.tengan.mall.wallet.domain.repository.WalletRuleRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

/**
 * 純試算、無副作用——結帳頁預覽用，不扣點數。真正核銷是 tengan-order 下單 Saga 呼叫的
 * internal {@code /internal/wallet/points/consume}（見 ConsumePointsService），這裡只回答
 * 「如果我用這麼多點，折抵多少錢、餘額夠不夠」。
 */
@Service
public class PreviewRedeemService implements PreviewRedeemUseCase {

    private final PointsTransactionRepository pointsTransactionRepository;
    private final WalletRuleRepository walletRuleRepository;

    public PreviewRedeemService(PointsTransactionRepository pointsTransactionRepository,
            WalletRuleRepository walletRuleRepository) {
        this.pointsTransactionRepository = pointsTransactionRepository;
        this.walletRuleRepository = walletRuleRepository;
    }

    @Override
    public PreviewRedeemResult preview(PreviewRedeemCommand command) {
        if (command.points() <= 0) {
            return new PreviewRedeemResult(false, BigDecimal.ZERO);
        }
        int balance = pointsTransactionRepository.sumConfirmedPoints(command.memberId());
        if (command.points() > balance) {
            return new PreviewRedeemResult(false, BigDecimal.ZERO);
        }
        var ratio = walletRuleRepository.get().getPointValueRatio();
        BigDecimal discount = ratio.multiply(BigDecimal.valueOf(command.points()));
        if (discount.compareTo(command.orderAmount()) > 0) {
            discount = command.orderAmount();
        }
        return new PreviewRedeemResult(true, discount);
    }
}
