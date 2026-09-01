package com.tengan.mall.auth.application.password;

public interface ForgotPasswordVerifyUseCase {

    ForgotPasswordVerifyResult verify(ForgotPasswordVerifyCommand command);
}
