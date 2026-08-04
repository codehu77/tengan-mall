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
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/**
 * 每次啟動產生一組新的 RSA keypair（不落地存檔）。重啟後 kid 改變，但 NimbusJwtDecoder 的
 * RemoteJWKSet 遇到不認得的 kid 會自動重新拉 /oauth2/jwks，不需要手動處理快取失效。
 *
 * <p>這是使用者 JWT 專用的金鑰，跟 {@link ServiceJwtKeyConfig} 的服務對服務金鑰分開——現在
 * context 裡有兩個 {@code JWKSource<SecurityContext>} bean，下面注入處用 {@code @Qualifier}
 * 明確指到這一組，避免 Spring 因為型別歧義而選錯（或直接啟動失敗）。</p>
 */
@Configuration
public class JwtKeyConfig {

    @Bean
    public RSAKey rsaKey() {
        KeyPair keyPair = generateKeyPair();
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public JWKSet jwkSet(RSAKey rsaKey) {
        return new JWKSet(rsaKey);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(JWKSet jwkSet) {
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtEncoder jwtEncoder(@Qualifier("jwkSource") JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
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
