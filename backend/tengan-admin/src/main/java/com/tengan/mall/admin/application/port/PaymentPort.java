package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-payment 的付款管理 internal 端點，跟 {@link OrderPort} 同樣的純代理原則（不重做業務規則）。 */
public interface PaymentPort {

    PaymentRecordPageResult listPaymentRecords(String orderSn, String method, int page, int pageSize);

    List<PaymentMethodConfigItem> listPaymentMethods();

    void updatePaymentMethodStatus(String method, boolean enabled, String operatorToken);
}
