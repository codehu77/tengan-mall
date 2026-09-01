package com.tengan.mall.auth.application.password;

public interface ForgotPasswordUseCase {

    /** 永遠不丟例外——帳號不存在/無密碼/無手機都靜默 no-op，由 controller 固定回同一句訊息防列舉。 */
    void forgot(ForgotPasswordCommand command);
}
