package com.tengan.mall.payment.application.subscription;

import com.tengan.mall.payment.application.port.WalletPort;
import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 全站第三個 @Scheduled 排程（跟 tengan-order 的 PointsGrantScheduler、tengan-wallet 的
 * PointsExpiryScheduler 同一種「排程掃描到期」模式）。**全專案唯一會員降級的地方**——取消訂閱
 * （顧客主動/連續失敗 6 次）都只標記 status=CANCELLED，不會直接降級，統一交給這裡在 paidUntil
 * 真的到期時才處理，讓使用者已經付過的那一期權益維持到底。
 */
@Component
public class SubscriptionExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionExpiryScheduler.class);
    private static final int BATCH_LIMIT = 200;

    private final SubscriptionRepository subscriptionRepository;
    private final WalletPort walletPort;

    public SubscriptionExpiryScheduler(SubscriptionRepository subscriptionRepository, WalletPort walletPort) {
        this.subscriptionRepository = subscriptionRepository;
        this.walletPort = walletPort;
    }

    @Scheduled(fixedDelayString = "${tengan.payment.subscription-expiry-scan-interval-ms:3600000}")
    public void expireDueSubscriptions() {
        var due = subscriptionRepository.findDueForExpiry(Instant.now(), BATCH_LIMIT);
        for (var subscription : due) {
            try {
                walletPort.downgradeTier(subscription.getMemberId(), "訂閱到期，paidUntil=" + subscription.getPaidUntil());
                subscriptionRepository.markBenefitExpired(subscription.getId());
            } catch (RuntimeException e) {
                log.error("訂閱到期降級失敗，subscriptionId={}，等下一輪排程重試", subscription.getId(), e);
            }
        }
    }
}
