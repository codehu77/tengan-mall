package com.tengan.mall.payment.infrastructure.linepay.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LinePayConfirmResponse(String returnCode, String returnMessage) {

    public boolean isSuccess() {
        return "0000".equals(returnCode);
    }
}
