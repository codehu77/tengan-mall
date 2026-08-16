package com.tengan.mall.wallet.application.points;

import com.tengan.mall.wallet.domain.model.MemberTierLevel;
import com.tengan.mall.wallet.domain.model.PointsTransaction;
import com.tengan.mall.wallet.domain.repository.MemberTierRepository;
import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import com.tengan.mall.wallet.domain.repository.WalletRuleRepository;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

/**
 * 訂單確認收貨後的非關鍵路徑呼叫（tengan-order 的 safely() 包裹，失敗不影響確認收貨本身）。
 * 先掛一筆 PENDING，讓「待入帳點數」在鑑賞期內就看得到；冪等以 orderSn 為鍵，用
 * {@link PointsTransactionRepository#findEarnByOrderSn} 查「任何狀態」的既有列才略過——
 * 只查 PENDING 的話，這筆一旦被 earn() 轉成 CONFIRMED，重複呼叫 reserve 就會誤判成「還沒 reserve
 * 過」而補插入一筆重複的列（曾經在手動測試時踩到這個坑：出貨沒有真實觸發鏈路要手動推進訂單狀態，
 * 重複呼叫確認收貨會導致「待入帳」跟「已確認」各一筆同一張訂單的回饋）。
 */
@Service
public class ReservePointsService implements ReservePointsUseCase {

    private final PointsTransactionRepository pointsTransactionRepository;
    private final MemberTierRepository memberTierRepository;
    private final WalletRuleRepository walletRuleRepository;

    public ReservePointsService(PointsTransactionRepository pointsTransactionRepository,
            MemberTierRepository memberTierRepository, WalletRuleRepository walletRuleRepository) {
        this.pointsTransactionRepository = pointsTransactionRepository;
        this.memberTierRepository = memberTierRepository;
        this.walletRuleRepository = walletRuleRepository;
    }

    @Override
    public void reserve(ReservePointsCommand command) {
        if (pointsTransactionRepository.findEarnByOrderSn(command.orderSn()).isPresent()) {
            return;
        }
        MemberTierLevel tier = memberTierRepository.findByMemberId(command.memberId())
                .map(t -> t.getTier()).orElse(MemberTierLevel.FREE);
        var rule = walletRuleRepository.get();
        int points = command.payAmount().multiply(rule.cashbackRateFor(tier))
                .setScale(0, RoundingMode.DOWN).intValue();
        if (points <= 0) {
            return;
        }
        // 標題不重複帶狀態字樣——PENDING/CONFIRMED 由 status 欄位單獨表達（confirmPending 轉正時只改
        // status 不改標題，之前標題寫死帶「（待確認）」會在轉正後留下跟 status 矛盾的殘留字樣）。
        var transaction = PointsTransaction.reserve(command.memberId(), points, command.orderSn(), "消費回饋",
                "訂單 " + command.orderSn() + " 完成，鑑賞期滿後正式入帳");
        pointsTransactionRepository.save(transaction);
    }
}
