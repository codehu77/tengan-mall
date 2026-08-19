package com.tengan.mall.payment.application.subscription;

import com.tengan.mall.payment.domain.model.Subscription;

public interface ReconcileSubscriptionUseCase {

    /**
     * 同步查詢一份卡在 PENDING 的訂閱首期是否真的授權成功，餵回既有的 ACTIVE 收斂邏輯或立即作廢
     * （釋放 uk_active_slot）。供使用者重新訂閱時（{@link SubscribeService}）跟之後的排程式查帳共用
     * 同一支底層邏輯。
     *
     * @return true 代表查到首期已成功（已收斂為 ACTIVE）；false 代表確認首期未成功（已作廢）。
     */
    boolean reconcile(Subscription pendingSubscription);
}
