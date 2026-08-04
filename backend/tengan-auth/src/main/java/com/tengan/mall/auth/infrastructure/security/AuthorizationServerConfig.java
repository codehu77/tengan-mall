package com.tengan.mall.auth.infrastructure.security;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 服務對服務（Client Credentials Grant）token 簽發：/oauth2/service/token、/oauth2/service/jwks。
 * 跟使用者登入用的三條鏈（{@link SecurityConfig}）分開、獨立金鑰（見 {@link ServiceJwtKeyConfig}），
 * @Order(0) 比既有三條鏈都優先，確保這組 matcher 先攔到自己的路徑
 * （微服務前台API待開發清單.md「服務間身份驗證」章節的落地）。
 */
@Configuration
public class AuthorizationServerConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .tokenEndpoint("/oauth2/service/token")
                .jwkSetEndpoint("/oauth2/service/jwks")
                .build();
    }

    /** 用獨立的服務金鑰簽發，不依賴 Spring 對 {@code JWKSource<SecurityContext>} 型別的隱式單一 bean 尋找。 */
    @Bean
    public OAuth2TokenGenerator<?> serviceTokenGenerator(
            @Qualifier("serviceJwkSource") JWKSource<SecurityContext> serviceJwkSource) {
        JwtEncoder serviceJwtEncoder = new NimbusJwtEncoder(serviceJwkSource);
        return new DelegatingOAuth2TokenGenerator(new JwtGenerator(serviceJwtEncoder),
                new OAuth2AccessTokenGenerator());
    }

    /**
     * context 裡有兩個 {@code JWKSource<SecurityContext>} bean（使用者金鑰 + 服務金鑰），Spring
     * Authorization Server 內部組 JWKS 端點 filter 時是自己呼叫
     * {@code ApplicationContext.getBeanNamesForType(...)}，找到超過一個同型別 bean 就直接丟
     * {@code NoUniqueBeanDefinitionException}——這段邏輯不看 {@code @Primary}（試過了，不會生效）。
     * 但它會先檢查 {@code HttpSecurity} 的 shared object 快取，有值就直接用、不會走那段模糊查找，
     * 所以在套用 configurer 之前手動塞一次，跳過那個問題根源。
     */
    @Bean
    @Order(0)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
            AuthorizationServerSettings authorizationServerSettings, OAuth2TokenGenerator<?> serviceTokenGenerator,
            @Qualifier("serviceJwkSource") JWKSource<SecurityContext> serviceJwkSource) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        http.setSharedObject(JWKSource.class, serviceJwkSource);

        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .csrf(csrf -> csrf.disable())
                .with(authorizationServerConfigurer, configurer -> configurer.tokenGenerator(serviceTokenGenerator))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(authorizationServerSettings.getJwkSetEndpoint()).permitAll()
                        .anyRequest().authenticated());
        return http.build();
    }
}
