package com.tengan.mall.payment.application.webhook;

import java.util.Map;

public record HandleEcpayCallbackCommand(Map<String, String> params) {
}
