package com.tengan.mall.payment.application.payment;

import com.tengan.mall.payment.domain.repository.PaymentRecordRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Phase 8.6 原本設計的排程式查帳（情境 A：一般訂單 credit_card 卡在 PENDING）。跟 tengan-order
 * PointsGrantScheduler、tengan-wallet PointsExpiryScheduler、同模組 SubscriptionExpiryScheduler
 * 同一種「排程掃描到期」模式。找候選後直接呼叫 Phase 8.6 擴充已經寫好的 {@link ReconcilePaymentUseCase}，
 * 不重寫收斂邏輯——跟使用者手動重試付款觸發的是同一支底層方法，差別只在觸發時機跟候選門檻。
 */
@Component
public class PaymentReconcileScheduler {

    private static final Logger log = LoggerFactory.getLogger(PaymentReconcileScheduler.class);
    private static final int BATCH_LIMIT = 200;

    /** ECPay 官方文件明講「未收到通知超過 40 分鐘才該查」，太早查是白工。 */
    private static final long STUCK_THRESHOLD_MINUTES = 40;

    private final PaymentRecordRepository paymentRecordRepository;
    private final ReconcilePaymentUseCase reconcilePaymentUseCase;

    public PaymentReconcileScheduler(PaymentRecordRepository paymentRecordRepository,
            ReconcilePaymentUseCase reconcilePaymentUseCase) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.reconcilePaymentUseCase = reconcilePaymentUseCase;
    }

    // ECPay 官方文件對查詢 API 有速率限制（太快呼叫回 403，需間隔至少 30 分鐘才能再查同一筆），預設
    // 掃描間隔訂 30 分鐘：正常情況下每筆記錄查一次就會離開 PENDING（轉 PAID/FAILED），不會被重複查；
    // 只有查詢本身失敗（例如網路問題）記錄才會留在 PENDING 給下一輪重試，此時才需要顧到這個節流下限。
    @Scheduled(fixedDelayString = "${tengan.payment.reconcile-scan-interval-ms:1800000}")
    public void reconcileStuckPending() {
        Instant cutoff = Instant.now().minus(STUCK_THRESHOLD_MINUTES, ChronoUnit.MINUTES);
        var candidates = paymentRecordRepository.findStuckPending(cutoff, BATCH_LIMIT);
        for (var candidate : candidates) {
            try {
                reconcilePaymentUseCase.reconcile(candidate);
            } catch (RuntimeException e) {
                log.error("排程查帳失敗，paymentRecordId={}，等下一輪排程重試", candidate.getId(), e);
            }
        }
    }
}
