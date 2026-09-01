package com.tengan.mall.auth.domain.exception;

/**
 * 方向B的核心錯誤（見 oauth_login_design/login_register_redesign 定案）：偵測到 Google 回傳的
 * email 跟既有密碼帳號相符時，不自動合併，導使用者回登入頁，登入後才能在會員中心手動連結。
 */
public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String email) {
        super("此 Email 已註冊，請先登入，登入後可在會員中心連結 Google 帳號: " + email);
    }
}
