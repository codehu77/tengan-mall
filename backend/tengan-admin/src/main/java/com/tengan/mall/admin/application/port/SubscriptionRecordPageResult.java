package com.tengan.mall.admin.application.port;

import java.util.List;

public record SubscriptionRecordPageResult(List<SubscriptionRecordItem> items, long total) {
}
