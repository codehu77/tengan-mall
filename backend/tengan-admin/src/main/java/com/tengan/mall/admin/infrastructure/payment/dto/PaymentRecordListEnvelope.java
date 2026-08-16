package com.tengan.mall.admin.infrastructure.payment.dto;

import com.tengan.mall.admin.application.port.PaymentRecordItem;
import java.util.List;

public record PaymentRecordListEnvelope(List<PaymentRecordItem> items, long total) {
}
