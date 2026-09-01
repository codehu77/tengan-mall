package com.tengan.mall.auth.application.port;

/**
 * 驗證第三方登入憑證（Google 用 GIS ID Token、Facebook 未來用 Graph API access token），
 * 驗證失敗（簽章/過期/audience 不符）丟 {@code InvalidOAuthTokenException}。
 */
public interface OAuthProviderVerifier {

    OAuthUserInfo verify(String token);
}
