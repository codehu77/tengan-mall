package com.tengan.mall.auth.application.password;

public interface ResetPasswordUseCase {

    ResetPasswordResult reset(ResetPasswordCommand command);
}
