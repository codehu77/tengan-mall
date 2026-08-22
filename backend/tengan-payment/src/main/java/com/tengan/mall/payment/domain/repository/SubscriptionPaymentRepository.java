package com.tengan.mall.payment.domain.repository;

import com.tengan.mall.payment.domain.model.SubscriptionPayment;
import java.util.List;

public interface SubscriptionPaymentRepository {

    SubscriptionPayment save(SubscriptionPayment payment);

    /** 冪等防重複處理同一筆 ECPay 通知。 */
    boolean existsByGwsr(String gwsr);

    /** 後台訂閱管理明細用：某份訂閱的完整扣款通知歷史，依 processDate 舊到新排序。 */
    List<SubscriptionPayment> findBySubscriptionId(Long subscriptionId);
}
