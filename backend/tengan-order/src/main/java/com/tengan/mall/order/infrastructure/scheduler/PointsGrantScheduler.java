package com.tengan.mall.order.infrastructure.scheduler;

import com.tengan.mall.order.application.order.OrderQueryPort;
import com.tengan.mall.order.application.port.WalletPort;
import com.tengan.mall.order.domain.repository.OrderRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 全專案第一個真正的 @Scheduled 排程（過去的時間延遲一律靠 RabbitMQ TTL+DLX，鑑賞期這種
 * 「查詢+批次處理」語意的長延遲改用排程更穩健，見 .docs 開發順序清單 Phase 8「開發細節」）。
 *
 * <p>鑑賞期分鐘數（grace_period_minutes）現查 tengan-wallet 的 wallet_rule（可後台即時調整），
 * 不快取在本服務——每次排程執行都反映當下最新設定。原本設計成「天」為單位，demo 時發現顆粒度太粗
 * （最小非零值就是 1 天），改成「分鐘」方便展示時調成 1、2 分鐘。</p>
 */
@Component
public class PointsGrantScheduler {

    private static final Logger log = LoggerFactory.getLogger(PointsGrantScheduler.class);
    private static final int BATCH_LIMIT = 200;

    private final OrderQueryPort orderQueryPort;
    private final OrderRepository orderRepository;
    private final WalletPort walletPort;

    public PointsGrantScheduler(OrderQueryPort orderQueryPort, OrderRepository orderRepository,
            WalletPort walletPort) {
        this.orderQueryPort = orderQueryPort;
        this.orderRepository = orderRepository;
        this.walletPort = walletPort;
    }

    @Scheduled(fixedDelayString = "${tengan.order.points-grant-scan-interval-ms:3600000}")
    public void grantDuePoints() {
        int gracePeriodMinutes;
        try {
            gracePeriodMinutes = walletPort.getGracePeriodMinutes();
        } catch (RuntimeException e) {
            log.error("查詢 wallet_rule 鑑賞期分鐘數失敗，本輪排程跳過，等下一輪重試", e);
            return;
        }
        Instant cutoff = Instant.now().minusSeconds((long) gracePeriodMinutes * 60);
        var candidates = orderQueryPort.findPendingPointsCredit(cutoff, BATCH_LIMIT);
        for (var candidate : candidates) {
            try {
                walletPort.earn(candidate.memberId(), candidate.orderSn(), candidate.payAmount());
                orderRepository.markPointsCredited(candidate.orderSn());
            } catch (RuntimeException e) {
                log.error("點數入帳失敗，orderSn={}，等下一輪排程重試", candidate.orderSn(), e);
            }
        }
    }
}
