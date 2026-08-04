package com.tengan.mall.auth.application.sms;

public interface VerifySmsCodeUseCase {

    /** 只做 UX 預檢查（不消耗驗證碼），最終授權判斷在 register 時做真正的 verifyAndConsume。 */
    boolean verify(VerifySmsCodeCommand command);
}
