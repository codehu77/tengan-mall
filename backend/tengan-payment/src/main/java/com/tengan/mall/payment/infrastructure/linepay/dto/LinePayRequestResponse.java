package com.tengan.mall.payment.infrastructure.linepay.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LinePayRequestResponse(String returnCode, String returnMessage, Info info) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Info(PaymentUrl paymentUrl, long transactionId) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PaymentUrl(String web, String app) {
    }
}
