package com.tengan.mall.payment.application.subscription;

import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Phase 8.6 原本設計的排程式查帳（情境 A：訂閱首期扣款卡在 PENDING），跟同模組
 * {@link PaymentReconcileScheduler 一般訂單版本} 是同一種模式，找候選後呼叫已經寫好的
 * {@link ReconcileSubscriptionUseCase}。情境 B（ACTIVE 續期通知完全沒來）不在這支排程範圍內，
 * 目前仍是已知限制。
 */
@Component
public class SubscriptionReconcileScheduler {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionReconcileScheduler.class);
    private static final int BATCH_LIMIT = 200;

    /** 跟 PaymentReconcileScheduler 用同一個 ECPay 官方建議門檻。 */
    private static final long STUCK_THRESHOLD_MINUTES = 40;

    private final SubscriptionRepository subscriptionRepository;
    private final ReconcileSubscriptionUseCase reconcileSubscriptionUseCase;

    public SubscriptionReconcileScheduler(SubscriptionRepository subscriptionRepository,
            ReconcileSubscriptionUseCase reconcileSubscriptionUseCase) {
        this.subscriptionRepository = subscriptionRepository;
        this.reconcileSubscriptionUseCase = reconcileSubscriptionUseCase;
    }

    // 同 PaymentReconcileScheduler，顧到 ECPay 查詢 API 至少間隔 30 分鐘的速率限制。
    @Scheduled(fixedDelayString = "${tengan.payment.subscription-reconcile-scan-interval-ms:1800000}")
    public void reconcileStuckPending() {
        Instant cutoff = Instant.now().minus(STUCK_THRESHOLD_MINUTES, ChronoUnit.MINUTES);
        var candidates = subscriptionRepository.findStuckPending(cutoff, BATCH_LIMIT);
        for (var candidate : candidates) {
            try {
                reconcileSubscriptionUseCase.reconcile(candidate);
            } catch (RuntimeException e) {
                log.error("排程查帳失敗，subscriptionId={}，等下一輪排程重試", candidate.getId(), e);
            }
        }
    }
}
