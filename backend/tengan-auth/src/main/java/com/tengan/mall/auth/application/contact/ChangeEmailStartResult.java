package com.tengan.mall.auth.application.contact;

/** 展示模式：驗證碼直接回在結果裡（刻意不接真實郵件商），同時寫進 log。 */
public record ChangeEmailStartResult(String code) {
}
