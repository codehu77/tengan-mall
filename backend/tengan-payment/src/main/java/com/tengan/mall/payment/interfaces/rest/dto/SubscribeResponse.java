package com.tengan.mall.payment.interfaces.rest.dto;

public record SubscribeResponse(Long subscriptionId, EcpayFormResponse ecpayForm) {
}
