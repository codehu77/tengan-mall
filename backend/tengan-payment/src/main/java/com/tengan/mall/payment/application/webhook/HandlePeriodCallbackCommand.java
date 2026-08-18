package com.tengan.mall.payment.application.webhook;

import java.util.Map;

public record HandlePeriodCallbackCommand(Map<String, String> params) {
}
