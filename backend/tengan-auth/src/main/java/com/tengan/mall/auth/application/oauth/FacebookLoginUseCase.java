package com.tengan.mall.auth.application.oauth;

public interface FacebookLoginUseCase {

    FacebookLoginResult login(FacebookLoginCommand command);
}
