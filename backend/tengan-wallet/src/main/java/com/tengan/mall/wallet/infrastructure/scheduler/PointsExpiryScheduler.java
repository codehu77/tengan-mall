package com.tengan.mall.wallet.infrastructure.scheduler;

import com.tengan.mall.wallet.domain.model.PointsTransaction;
import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 365 天點數到期（wallet_rule.point_expiry_days 決定，可後台即時調整）。完全自包含在本服務內，
 * 不跨服務——掃描自己的 points_transaction，找已過期但還沒沖銷過的 EARN 批次，逐筆插入一筆負向
 * EXPIRE 列（見 PointsTransaction 類別說明：不是把原始 EARN 列狀態改掉，是另開一筆沖銷交易）。
 */
@Component
public class PointsExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(PointsExpiryScheduler.class);
    private static final int BATCH_LIMIT = 200;

    private final PointsTransactionRepository pointsTransactionRepository;

    public PointsExpiryScheduler(PointsTransactionRepository pointsTransactionRepository) {
        this.pointsTransactionRepository = pointsTransactionRepository;
    }

    @Scheduled(fixedDelayString = "${tengan.wallet.expiry-scan-interval-ms:3600000}")
    public void expireDuePoints() {
        var expirable = pointsTransactionRepository.findExpirableEarnBatches(Instant.now(), BATCH_LIMIT);
        for (PointsTransaction earn : expirable) {
            try {
                var expireTx = PointsTransaction.expire(earn.getMemberId(), earn.getPoints(), earn.getId(), "點數到期",
                        "第 " + earn.getId() + " 筆點數（原核發於 " + earn.getCreatedAt() + "）已到期");
                pointsTransactionRepository.save(expireTx);
            } catch (RuntimeException e) {
                log.error("點數到期沖銷失敗，earnTransactionId={}，等下一輪排程重試", earn.getId(), e);
            }
        }
    }
}
