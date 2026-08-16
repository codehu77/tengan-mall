package com.tengan.mall.payment.domain.model;

/** method/enabled 兩欄的簡單設定列，不用獨立聚合根複雜度（比照舊 plan 對這個概念的判斷）。 */
public record PaymentMethodConfig(String method, boolean enabled) {
}
