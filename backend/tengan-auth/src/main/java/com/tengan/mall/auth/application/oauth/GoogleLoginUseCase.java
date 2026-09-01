package com.tengan.mall.auth.application.oauth;

public interface GoogleLoginUseCase {

    GoogleLoginResult login(GoogleLoginCommand command);
}
