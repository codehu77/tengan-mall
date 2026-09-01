package com.tengan.mall.auth.interfaces.rest.dto;

/** 不管 service 內部是否真的發了 OTP，永遠回同一句訊息，防止帳號列舉。 */
public record ForgotPasswordResponse(String message) {
}
