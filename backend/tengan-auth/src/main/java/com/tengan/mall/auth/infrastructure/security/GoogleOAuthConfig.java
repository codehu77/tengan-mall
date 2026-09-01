package com.tengan.mall.auth.infrastructure.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Google GIS ID Token 驗證用的 JwtDecoder，跟使用者/服務兩把自簽金鑰（{@link JwtKeyConfig}/
 * {@link ServiceJwtKeyConfig}）完全獨立——這把不是自己簽的，是驗證 Google 簽的 token，透過
 * {@link JwtDecoders#fromIssuerLocation} 做 OIDC discovery 自動抓 Google 的 JWKS/issuer 設定。
 * 該方法預設不驗 audience，額外疊加一個檢查 aud claim 等於自己 Client ID 的 validator，
 * 避免拿別的應用程式簽發的 Google ID Token 冒用登入。
 */
@Configuration
public class GoogleOAuthConfig {

    private static final String GOOGLE_ISSUER = "https://accounts.google.com";

    /** bean 名稱即 "googleJwtDecoder"，注入處用同名參數即可比對到（比照 userJwtDecoder/serviceJwtDecoder 的既有模式）。 */
    @Bean
    public JwtDecoder googleJwtDecoder(@Value("${google.oauth.client-id}") String clientId) {
        NimbusJwtDecoder decoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(GOOGLE_ISSUER);
        OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefaultWithIssuer(GOOGLE_ISSUER);
        OAuth2TokenValidator<Jwt> audienceValidator = audienceValidator(clientId);
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaultValidators, audienceValidator));
        return decoder;
    }

    private OAuth2TokenValidator<Jwt> audienceValidator(String clientId) {
        return jwt -> {
            List<String> audience = jwt.getAudience();
            if (audience != null && audience.contains(clientId)) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "aud claim 與設定的 Google Client ID 不符", null));
        };
    }
}
