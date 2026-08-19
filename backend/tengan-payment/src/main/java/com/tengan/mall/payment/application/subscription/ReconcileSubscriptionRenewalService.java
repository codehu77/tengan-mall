package com.tengan.mall.payment.application.subscription;

import com.tengan.mall.payment.application.webhook.HandlePeriodCallbackUseCase;
import com.tengan.mall.payment.domain.model.Subscription;
import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import com.tengan.mall.payment.infrastructure.ecpay.EcpayPaymentGatewayClient;
import java.time.Instant;
import java.util.Comparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReconcileSubscriptionRenewalService implements ReconcileSubscriptionRenewalUseCase {

    /** ECPay 這邊已經自己終止定期定額約定，不會再嘗試扣款——見 EcpayPeriodExecLogQueryResult javadoc。 */
    private static final String EXEC_STATUS_TERMINATED = "0";

    private final EcpayPaymentGatewayClient ecpayPaymentGatewayClient;
    private final SubscriptionRepository subscriptionRepository;
    private final HandlePeriodCallbackUseCase handlePeriodCallbackUseCase;

    public ReconcileSubscriptionRenewalService(EcpayPaymentGatewayClient ecpayPaymentGatewayClient,
            SubscriptionRepository subscriptionRepository, HandlePeriodCallbackUseCase handlePeriodCallbackUseCase) {
        this.ecpayPaymentGatewayClient = ecpayPaymentGatewayClient;
        this.subscriptionRepository = subscriptionRepository;
        this.handlePeriodCallbackUseCase = handlePeriodCallbackUseCase;
    }

    @Override
    @Transactional
    public boolean reconcile(Subscription staleActiveSubscription) {
        String merchantTradeNo = staleActiveSubscription.getEcpayMerchantTradeNo();
        Instant paidUntilBefore = staleActiveSubscription.getPaidUntil();

        var queried = ecpayPaymentGatewayClient.queryPeriodExecLog(merchantTradeNo);
        var sortedEntries = queried.entries().stream().sorted(Comparator.comparing(EcpayPeriodExecLogEntry::processDate))
                .toList();

        // 依時間序重播，successCount 自己累計而不是直接用 ECPay 頂層 TotalSuccessTimes——那個欄位
        // 語意是「查詢當下的累計值」，用它會讓中間某一期重播時的 totalSuccessTimes 跟該期實際發生時
        // 的累計次數對不上；自己逐筆數才能保證跟 applyResult()/paidUntil 計算的既有語意一致。
        int successCount = 0;
        for (var entry : sortedEntries) {
            if (entry.success()) {
                successCount++;
            }
            handlePeriodCallbackUseCase.replayPeriodResult(merchantTradeNo, entry.gwsr(), entry.success(),
                    entry.amount(), successCount, entry.processDate());
        }

        var refreshed = subscriptionRepository.findByMerchantTradeNo(merchantTradeNo);
        boolean recovered = refreshed.map(s -> s.getPaidUntil().isAfter(paidUntilBefore)).orElse(false);
        boolean stillStale = refreshed.map(s -> s.getPaidUntil().isBefore(Instant.now())).orElse(true);

        // 重播完 ExecLog 還是沒有新進展，且 ECPay 自己也確認已終止——不用再等下一輪排程，直接視同失效。
        // 連續失敗 6 次自動停用（既有邏輯，走 applyResult 內建）處理的是「有失敗通知」的情況，這裡補的是
        // 「ECPay 已終止但我們完全沒收到任何失敗通知」的殘餘缺口。
        if (!recovered && stillStale && EXEC_STATUS_TERMINATED.equals(queried.execStatus())) {
            subscriptionRepository.markCancelled(staleActiveSubscription.getId());
        }
        return recovered;
    }
}
