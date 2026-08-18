package com.tengan.mall.payment.application.webhook;

import java.util.Map;

public record HandleSubscriptionReturnCallbackCommand(Map<String, String> params) {
}
