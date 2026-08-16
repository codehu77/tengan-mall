package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record PaymentRecordListResponse(List<PaymentRecordResponse> items, long total) {
}
