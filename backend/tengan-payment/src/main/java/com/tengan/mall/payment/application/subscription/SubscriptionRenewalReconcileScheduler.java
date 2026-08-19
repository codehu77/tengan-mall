package com.tengan.mall.payment.application.subscription;

import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 情境 B（2026-08-19 補上，先前規劃階段刻意留白的已知限制）：ACTIVE 訂閱卡在 paidUntil 已過期，
 * 續期的 PeriodReturnURL 通知遲遲沒進來。跟情境 A 的 {@link PaymentReconcileScheduler}/
 * {@link SubscriptionReconcileScheduler} 同一種排程模式，差別是候選門檻——正常情況下 PeriodReturnURL
 * 本來就可能延遲到隔天才到（實測經驗），閾值要比情境 A 的 40 分鐘寬鬆很多，避免誤判正常延遲。
 */
@Component
public class SubscriptionRenewalReconcileScheduler {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionRenewalReconcileScheduler.class);
    private static final int BATCH_LIMIT = 200;

    private final SubscriptionRepository subscriptionRepository;
    private final ReconcileSubscriptionRenewalUseCase reconcileSubscriptionRenewalUseCase;
    private final int graceHours;

    public SubscriptionRenewalReconcileScheduler(SubscriptionRepository subscriptionRepository,
            ReconcileSubscriptionRenewalUseCase reconcileSubscriptionRenewalUseCase,
            @Value("${tengan.payment.subscription-renewal-grace-hours:24}") int graceHours) {
        this.subscriptionRepository = subscriptionRepository;
        this.reconcileSubscriptionRenewalUseCase = reconcileSubscriptionRenewalUseCase;
        this.graceHours = graceHours;
    }

    // 跟情境 A 兩支排程一樣顧到 ECPay 查詢 API 至少間隔 30 分鐘的速率限制。
    @Scheduled(fixedDelayString = "${tengan.payment.subscription-renewal-reconcile-scan-interval-ms:1800000}")
    public void reconcileStuckActive() {
        Instant cutoff = Instant.now().minus(graceHours, ChronoUnit.HOURS);
        var candidates = subscriptionRepository.findStuckActive(cutoff, BATCH_LIMIT);
        for (var candidate : candidates) {
            try {
                reconcileSubscriptionRenewalUseCase.reconcile(candidate);
            } catch (RuntimeException e) {
                log.error("續期查帳失敗，subscriptionId={}，等下一輪排程重試", candidate.getId(), e);
            }
        }
    }
}
