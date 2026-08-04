package com.tengan.mall.jwt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * 每個服務只要加這個 starter 依賴 + 在 application.yml 設定 tengan.jwt.user.jwk-set-uri /
 * tengan.jwt.service.jwk-set-uri，就能拿到對應的 JwtDecoder bean，不用各自重寫驗簽邏輯
 * （見 docs/JWT設計.md「減少『每個服務都要重新設定一套 security』的方法」）。
 *
 * <p>兩顆 bean 各自用 @ConditionalOnProperty 控制是否產生：純服務間端點（Resource Server
 * 但不需要驗使用者身份的服務）不需要 userJwtDecoder，反之亦然。</p>
 */
@Configuration
@EnableConfigurationProperties(TenganJwtProperties.class)
public class JwtVerificationAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "tengan.jwt.user", name = "jwk-set-uri")
    public JwtDecoder userJwtDecoder(TenganJwtProperties properties) {
        return NimbusJwtDecoder
                .withJwkSetUri(properties.getUser().getJwkSetUri())
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "tengan.jwt.admin", name = "jwk-set-uri")
    public JwtDecoder adminJwtDecoder(TenganJwtProperties properties) {
        return NimbusJwtDecoder
                .withJwkSetUri(properties.getAdmin().getJwkSetUri())
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "tengan.jwt.service", name = "jwk-set-uri")
    public JwtDecoder serviceJwtDecoder(TenganJwtProperties properties) {
        return NimbusJwtDecoder
                .withJwkSetUri(properties.getService().getJwkSetUri())
                .build();
    }

    /** 驗證 customer 主體轉發的 X-Identity-Assertion（目前還沒有服務實際消費這個 bean）。 */
    @Bean
    @ConditionalOnProperty(prefix = "tengan.jwt.user", name = "jwk-set-uri")
    public IdentityAssertionVerifier userIdentityAssertionVerifier(JwtDecoder userJwtDecoder) {
        return new IdentityAssertionVerifier(userJwtDecoder);
    }

    /** 驗證 tengan-admin 後台 BFF 轉發的 X-Identity-Assertion（`tengan-admin` 呼叫 `/internal/**` 時附帶）。 */
    @Bean
    @ConditionalOnProperty(prefix = "tengan.jwt.admin", name = "jwk-set-uri")
    public IdentityAssertionVerifier adminIdentityAssertionVerifier(JwtDecoder adminJwtDecoder) {
        return new IdentityAssertionVerifier(adminJwtDecoder);
    }
}
