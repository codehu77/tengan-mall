package com.tengan.mall.payment.interfaces.rest.dto;

import java.util.List;

public record SubscriptionRecordListResponse(List<SubscriptionRecordResponse> items, long total) {
}
