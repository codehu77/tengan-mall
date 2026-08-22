package com.tengan.mall.payment.application.admin;

import java.util.List;

public record ListSubscriptionsResult(List<SubscriptionView> items, long total) {
}
