package com.tengan.mall.payment.application.subscription;

import com.tengan.mall.payment.domain.model.Subscription;

public interface ReconcileSubscriptionRenewalUseCase {

    /**
     * 情境 B：一份 ACTIVE 訂閱卡在 paidUntil 已過期，但續期的 PeriodReturnURL 通知遲遲沒進來（延遲、
     * 遺失、或像 ngrok 網址失效這種永久收不到的情況）。查 ECPay 完整執行紀錄（ExecLog），把還沒處理過
     * 的每一期重播回既有的 webhook 收斂邏輯，一次補回可能積欠的多期。
     *
     * @return true 代表這次查帳讓 paidUntil 真的往後延長了（確認有續訂成功）；false 代表沒有新進展
     *         （可能真的還沒到下一期扣款時間，也可能扣款失敗但還沒達到自動停用門檻）。
     */
    boolean reconcile(Subscription staleActiveSubscription);
}
