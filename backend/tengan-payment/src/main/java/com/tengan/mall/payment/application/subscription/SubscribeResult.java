package com.tengan.mall.payment.application.subscription;

import com.tengan.mall.payment.application.payment.EcpayFormData;

public record SubscribeResult(Long subscriptionId, EcpayFormData ecpayForm) {
}
