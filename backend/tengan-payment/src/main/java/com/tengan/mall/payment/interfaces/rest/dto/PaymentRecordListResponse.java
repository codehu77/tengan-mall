package com.tengan.mall.payment.interfaces.rest.dto;

import java.util.List;

public record PaymentRecordListResponse(List<PaymentRecordResponse> items, long total) {
}
