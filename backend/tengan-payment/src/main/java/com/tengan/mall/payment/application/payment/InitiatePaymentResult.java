package com.tengan.mall.payment.application.payment;

/**
 * method 分流各自只會填其中一組欄位：credit_card 填 ecpayForm；linepay 填 linePayPaymentUrl；
 * cod 兩者皆 null（前端看到兩者皆空就知道已經同步付款完成，直接導去訂單詳情頁）。
 */
public record InitiatePaymentResult(String method, EcpayFormData ecpayForm, String linePayPaymentUrl) {
}
