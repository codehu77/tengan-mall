package com.tengan.mall.auth.domain.exception;

/**
 * 方向B的核心錯誤（見 oauth_login_design/login_register_redesign 定案）：偵測到第三方登入回傳的
 * email 跟既有帳號相符時，不自動合併，導使用者回登入頁，登入後才能在會員中心手動連結。
 * {@link com.tengan.mall.auth.application.oauth.GoogleLoginService}/
 * {@link com.tengan.mall.auth.application.oauth.FacebookLoginService} 共用這個例外類別，訊息
 * 刻意不指名是哪個 provider 觸發的，避免文字寫死跟實際觸發來源對不上。
 */
public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String email) {
        super("此 Email 已註冊，請先登入，登入後可在會員中心「帳號安全」頁面連結第三方帳號: " + email);
    }
}
