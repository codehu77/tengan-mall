package com.tengan.mall.admin.infrastructure.payment.dto;

import com.tengan.mall.admin.application.port.SubscriptionRecordItem;
import java.util.List;

public record SubscriptionRecordListEnvelope(List<SubscriptionRecordItem> items, long total) {
}
