package com.tengan.mall.auth.application.oauth;

public interface LineLoginUseCase {

    LineLoginResult login(LineLoginCommand command);
}
