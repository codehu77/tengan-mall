package com.tengan.mall.auth.application.login;

public interface LoginUseCase {

    LoginResult login(LoginCommand command);
}
