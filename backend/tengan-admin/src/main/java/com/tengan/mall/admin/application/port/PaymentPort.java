package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-payment 的付款管理 internal 端點，跟 {@link OrderPort} 同樣的純代理原則（不重做業務規則）。 */
public interface PaymentPort {

    PaymentRecordPageResult listPaymentRecords(String orderSn, String method, int page, int pageSize);

    List<PaymentMethodConfigItem> listPaymentMethods();

    void updatePaymentMethodStatus(String method, boolean enabled, String operatorToken);

    /** 立即查帳（跳過排程的 40 分鐘門檻，全部目前卡在 PENDING 的記錄都查一次），供 demo 用手動按鈕。 */
    ReconcileNowResult triggerReconcileNow();
}
