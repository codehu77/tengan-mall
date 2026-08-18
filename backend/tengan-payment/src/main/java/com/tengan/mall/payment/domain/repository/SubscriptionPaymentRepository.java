package com.tengan.mall.payment.domain.repository;

import com.tengan.mall.payment.domain.model.SubscriptionPayment;

public interface SubscriptionPaymentRepository {

    SubscriptionPayment save(SubscriptionPayment payment);

    /** 冪等防重複處理同一筆 ECPay 通知。 */
    boolean existsByGwsr(String gwsr);
}
