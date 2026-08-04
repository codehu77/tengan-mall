package com.tengan.mall.auth.infrastructure.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 服務對服務（Client Credentials）token 專用的獨立金鑰組，跟 {@link JwtKeyConfig} 的使用者 JWT
 * 金鑰完全分開（微服務前台API待開發清單.md「服務間身份驗證」：兩者簽章金鑰各自獨立、互不影響）。
 * 對外走 /oauth2/service/jwks（見 AuthorizationServerConfig），不是 /oauth2/jwks。
 */
@Configuration
public class ServiceJwtKeyConfig {

    @Bean(name = "serviceRsaKey")
    public RSAKey serviceRsaKey() {
        KeyPair keyPair = generateKeyPair();
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean(name = "serviceJwkSet")
    public JWKSet serviceJwkSet(@Qualifier("serviceRsaKey") RSAKey serviceRsaKey) {
        return new JWKSet(serviceRsaKey);
    }

    @Bean(name = "serviceJwkSource")
    public JWKSource<SecurityContext> serviceJwkSource(@Qualifier("serviceJwkSet") JWKSet serviceJwkSet) {
        return new ImmutableJWKSet<>(serviceJwkSet);
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("RSA KeyPairGenerator 不可用", e);
        }
    }
}
