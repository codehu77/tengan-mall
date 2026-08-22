package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-payment 的訂閱管理 internal 端點，跟 {@link PaymentPort} 同樣的純代理原則（不重做業務規則）。 */
public interface SubscriptionPort {

    SubscriptionRecordPageResult listSubscriptions(Long memberId, Integer status, int page, int pageSize);

    List<SubscriptionPaymentItem> listSubscriptionPayments(Long subscriptionId);
}
