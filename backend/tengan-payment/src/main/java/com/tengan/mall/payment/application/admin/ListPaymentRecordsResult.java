package com.tengan.mall.payment.application.admin;

import java.util.List;

public record ListPaymentRecordsResult(List<PaymentRecordView> items, long total) {
}
