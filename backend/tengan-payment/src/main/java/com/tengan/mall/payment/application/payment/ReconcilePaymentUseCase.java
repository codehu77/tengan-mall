package com.tengan.mall.payment.application.payment;

import com.tengan.mall.payment.domain.model.PaymentRecord;

public interface ReconcilePaymentUseCase {

    /**
     * 同步查詢一筆卡在 PENDING 的 credit_card 付款嘗試，餵回既有的 PAID 收斂邏輯或標記 FAILED。
     * 供使用者重試時（{@link InitiatePaymentService}）跟之後的排程式查帳共用同一支底層邏輯。
     *
     * @return true 代表查到已付款成功（已收斂為 PAID）；false 代表確認未付款成功（已標記 FAILED）。
     */
    boolean reconcile(PaymentRecord pendingCreditCardRecord);
}
