package com.tengan.mall.auth.interfaces.rest.dto;

/** 展示模式：驗證碼直接回在 response 裡，不接真實簡訊/郵件商。 */
public record RegisterStartResponse(String code) {
}
