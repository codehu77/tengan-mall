package com.tengan.mall.wallet.application.points;

import com.tengan.mall.wallet.domain.model.MemberTierLevel;
import com.tengan.mall.wallet.domain.model.PointsTransaction;
import com.tengan.mall.wallet.domain.model.PointsTransactionStatus;
import com.tengan.mall.wallet.domain.repository.MemberTierRepository;
import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import com.tengan.mall.wallet.domain.repository.WalletRuleRepository;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 由 tengan-order 的 PointsGrantScheduler（鑑賞期 grace_period_minutes 過後）呼叫，冪等以 orderSn 為鍵
 * ——用 {@link PointsTransactionRepository#findEarnByOrderSn} 查「任何狀態」的既有列：已經是
 * CONFIRMED 就代表這筆已經入帳過（例如 tengan-order 呼叫成功但自己的 markPointsCredited 沒寫成功，
 * 導致排程下一輪重試），直接 no-op；還是 PENDING 就是正常路徑，套用當月封頂後轉 CONFIRMED；完全找不到
 * （reserve 當初非關鍵路徑失敗過）才用 payAmount 現算補建一筆直接 CONFIRMED 的列。只查 PENDING 的話，
 * 已經轉正的列會被誤判成「還沒 earn 過」而補建重複列，是曾經踩過的坑。
 *
 * <p>只要有既有 PENDING 列，一定要呼叫 confirmPending 結案，即使套用月封頂後 finalPoints 是 0——
 * tengan-order 的排程呼叫完這支之後，不管 wallet 這邊做了什麼都會把 order.points_credited 標記為
 * true、不會再重掃這張訂單，PENDING 列若沒被結案就會永久卡住、連下個月額度重置後也沒有機制回頭處理
 * （這是曾經踩過的坑：finalPoints<=0 時直接 return，PENDING 列從沒被結案過）。被封頂（無論封到剩多少
 * 或封到 0）時，description 改寫成說明原本可得多少、為什麼被砍——封頂不該是資料無聲消失。</p>
 */
@Service
public class EarnPointsService implements EarnPointsUseCase {

    private final PointsTransactionRepository pointsTransactionRepository;
    private final MemberTierRepository memberTierRepository;
    private final WalletRuleRepository walletRuleRepository;

    public EarnPointsService(PointsTransactionRepository pointsTransactionRepository,
            MemberTierRepository memberTierRepository, WalletRuleRepository walletRuleRepository) {
        this.pointsTransactionRepository = pointsTransactionRepository;
        this.memberTierRepository = memberTierRepository;
        this.walletRuleRepository = walletRuleRepository;
    }

    @Override
    @Transactional
    public void earn(EarnPointsCommand command) {
        var existing = pointsTransactionRepository.findEarnByOrderSn(command.orderSn());
        if (existing.isPresent() && existing.get().getStatus() == PointsTransactionStatus.CONFIRMED) {
            return;
        }

        var rule = walletRuleRepository.get();
        MemberTierLevel tier = memberTierRepository.findByMemberId(command.memberId())
                .map(t -> t.getTier()).orElse(MemberTierLevel.FREE);
        Instant monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        int alreadyThisMonth = pointsTransactionRepository.sumConfirmedEarnPointsSince(command.memberId(), monthStart);
        Integer cap = rule.monthlyCapFor(tier);

        int requestedPoints = existing.map(PointsTransaction::getPoints)
                .orElseGet(() -> command.payAmount() == null ? 0
                        : command.payAmount().multiply(rule.cashbackRateFor(tier)).setScale(0, RoundingMode.DOWN)
                                .intValue());

        if (existing.isEmpty() && requestedPoints <= 0) {
            return; // 補建路徑本來就沒有點數可入帳，沒有 PENDING 列需要結案
        }

        int finalPoints = cap == null ? requestedPoints : Math.max(0, Math.min(requestedPoints, cap - alreadyThisMonth));
        // 完全被封頂到 0 的話不設到期日——0 點沒有東西可以到期，帶一個到期日只會讓它被誤判成一筆
        // 「還在使用中、快到期」的有效批次（配合 PointsTransactionMapper 查詢層 points>0 的防護）。
        Instant expiresAt = finalPoints > 0
                ? Instant.now().plusSeconds((long) rule.getPointExpiryDays() * 24 * 3600)
                : null;
        int balanceAfter = pointsTransactionRepository.sumConfirmedPoints(command.memberId()) + finalPoints;

        if (existing.isPresent()) {
            String description = finalPoints < requestedPoints
                    ? "訂單 " + command.orderSn() + " 消費回饋原可得 " + requestedPoints + " 點，因本月已達 " + tier.name()
                            + " 會員回饋上限（" + cap + " 點），本筆僅入帳 " + finalPoints + " 點"
                    : "訂單 " + command.orderSn() + " 鑑賞期滿入帳";
            pointsTransactionRepository.confirmPending(command.orderSn(), finalPoints, balanceAfter, expiresAt,
                    "消費回饋", description);
        } else if (finalPoints > 0) {
            var transaction = PointsTransaction.earnDirect(command.memberId(), finalPoints, command.orderSn(),
                    "消費回饋", "訂單 " + command.orderSn() + " 鑑賞期滿入帳", expiresAt);
            transaction.assignBalanceAfter(balanceAfter);
            pointsTransactionRepository.save(transaction);
        }
    }
}
