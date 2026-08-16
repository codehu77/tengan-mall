package com.tengan.mall.payment.application.admin;

public record ListPaymentRecordsQuery(String orderSn, String method, int page, int pageSize) {
}
