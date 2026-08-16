package com.tengan.mall.payment.application.payment;

import java.util.Map;

/** 前端拿這個組一個隱藏 form，method=POST 直接 submit 導到 ECPay 付款頁。 */
public record EcpayFormData(String actionUrl, Map<String, String> fields) {
}
