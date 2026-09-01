package com.tengan.mall.auth.application.register;

/** 展示模式：驗證碼直接回在結果裡（刻意不接真實簡訊/郵件商），同時寫進 log。 */
public record RegisterStartResult(String code) {
}
