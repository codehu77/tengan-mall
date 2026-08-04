package com.tengan.mall.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 對應 application.yml 的 tengan.jwt.* 設定。
 * user（customer 主體）、admin（管理員主體）、service（S2S）三組各自獨立，任一組沒設定
 * jwk-set-uri 就不會產生對應的 JwtDecoder（見 {@link JwtVerificationAutoConfiguration} 的
 * @ConditionalOnProperty）。admin 這組目前只有 tengan-gateway 在用（要同時驗 tengan-auth 簽的
 * customer token 跟 tengan-admin 簽的 admin token，見 docs/JWT設計.md）——tengan-admin 自己
 * 驗自己簽的 token 時，沿用 user 這組即可，不需要另外設定 admin。
 */
@ConfigurationProperties(prefix = "tengan.jwt")
public class TenganJwtProperties {

    @NestedConfigurationProperty
    private final Decoder user = new Decoder();

    @NestedConfigurationProperty
    private final Decoder admin = new Decoder();

    @NestedConfigurationProperty
    private final Decoder service = new Decoder();

    public Decoder getUser() {
        return user;
    }

    public Decoder getAdmin() {
        return admin;
    }

    public Decoder getService() {
        return service;
    }

    public static class Decoder {
        /** 驗簽用的 JWK Set 端點，例如 tengan-auth 的 /oauth2/jwks */
        private String jwkSetUri;

        public String getJwkSetUri() {
            return jwkSetUri;
        }

        public void setJwkSetUri(String jwkSetUri) {
            this.jwkSetUri = jwkSetUri;
        }
    }
}
