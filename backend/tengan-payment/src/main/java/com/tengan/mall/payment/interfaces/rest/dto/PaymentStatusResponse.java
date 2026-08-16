package com.tengan.mall.payment.interfaces.rest.dto;

import java.math.BigDecimal;

public record PaymentStatusResponse(String orderSn, String method, int status, BigDecimal amount) {
}
