package com.tengan.mall.auth.application.sms;

public interface SendSmsCodeUseCase {

    /** 展示模式：刻意不接真實簡訊商，回傳值直接是驗證碼本身，同時寫進 log。 */
    String sendCode(SendSmsCodeCommand command);
}
