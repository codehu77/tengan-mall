package com.tengan.mall.payment.interfaces.rest.dto;

public record InitiatePaymentResponse(String method, EcpayFormResponse ecpayForm, String linePayPaymentUrl) {
}
