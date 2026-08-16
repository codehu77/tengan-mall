package com.tengan.mall.payment.interfaces.rest.dto;

import java.util.Map;

public record EcpayFormResponse(String actionUrl, Map<String, String> fields) {
}
