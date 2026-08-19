package com.tengan.mall.payment.application.reconcile;

import com.tengan.mall.payment.application.payment.ReconcilePaymentUseCase;
import com.tengan.mall.payment.application.subscription.ReconcileSubscriptionRenewalUseCase;
import com.tengan.mall.payment.application.subscription.ReconcileSubscriptionUseCase;
import com.tengan.mall.payment.domain.model.PaymentRecord;
import com.tengan.mall.payment.domain.model.Subscription;
import com.tengan.mall.payment.domain.repository.PaymentRecordRepository;
import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TriggerReconcileNowService implements TriggerReconcileNowUseCase {

    private static final Logger log = LoggerFactory.getLogger(TriggerReconcileNowService.class);
    private static final int BATCH_LIMIT = 200;

    private final PaymentRecordRepository paymentRecordRepository;
    private final ReconcilePaymentUseCase reconcilePaymentUseCase;
    private final SubscriptionRepository subscriptionRepository;
    private final ReconcileSubscriptionUseCase reconcileSubscriptionUseCase;
    private final ReconcileSubscriptionRenewalUseCase reconcileSubscriptionRenewalUseCase;

    public TriggerReconcileNowService(PaymentRecordRepository paymentRecordRepository,
            ReconcilePaymentUseCase reconcilePaymentUseCase, SubscriptionRepository subscriptionRepository,
            ReconcileSubscriptionUseCase reconcileSubscriptionUseCase,
            ReconcileSubscriptionRenewalUseCase reconcileSubscriptionRenewalUseCase) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.reconcilePaymentUseCase = reconcilePaymentUseCase;
        this.subscriptionRepository = subscriptionRepository;
        this.reconcileSubscriptionUseCase = reconcileSubscriptionUseCase;
        this.reconcileSubscriptionRenewalUseCase = reconcileSubscriptionRenewalUseCase;
    }

    @Override
    public ReconcileNowResult run() {
        Instant now = Instant.now();

        List<PaymentRecord> pendingPayments = paymentRecordRepository.findStuckPending(now, BATCH_LIMIT);
        int paymentConverged = 0;
        int paymentFailed = 0;
        for (var record : pendingPayments) {
            try {
                if (reconcilePaymentUseCase.reconcile(record)) {
                    paymentConverged++;
                } else {
                    paymentFailed++;
                }
            } catch (RuntimeException e) {
                log.error("立即查帳失敗，paymentRecordId={}", record.getId(), e);
            }
        }

        List<Subscription> pendingSubscriptions = subscriptionRepository.findStuckPending(now, BATCH_LIMIT);
        int subscriptionConverged = 0;
        int subscriptionFailed = 0;
        for (var subscription : pendingSubscriptions) {
            try {
                if (reconcileSubscriptionUseCase.reconcile(subscription)) {
                    subscriptionConverged++;
                } else {
                    subscriptionFailed++;
                }
            } catch (RuntimeException e) {
                log.error("立即查帳失敗，subscriptionId={}", subscription.getId(), e);
            }
        }

        List<Subscription> staleActiveSubscriptions = subscriptionRepository.findStuckActive(now, BATCH_LIMIT);
        int renewalRecovered = 0;
        for (var subscription : staleActiveSubscriptions) {
            try {
                if (reconcileSubscriptionRenewalUseCase.reconcile(subscription)) {
                    renewalRecovered++;
                }
            } catch (RuntimeException e) {
                log.error("立即查帳失敗，subscriptionId={}", subscription.getId(), e);
            }
        }

        return new ReconcileNowResult(pendingPayments.size(), paymentConverged, paymentFailed,
                pendingSubscriptions.size(), subscriptionConverged, subscriptionFailed,
                staleActiveSubscriptions.size(), renewalRecovered);
    }
}
