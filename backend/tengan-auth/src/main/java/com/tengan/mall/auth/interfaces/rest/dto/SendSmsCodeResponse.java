package com.tengan.mall.auth.interfaces.rest.dto;

/** 展示模式：驗證碼直接回在 response 裡（見 SendSmsCodeService），不接真實簡訊商。 */
public record SendSmsCodeResponse(String code) {
}
