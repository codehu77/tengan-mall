package com.tengan.mall.payment.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * zero-trust：/api/customer/** 直接用 userJwtDecoder 驗證，比照 tengan-coupon 既有模式。
 *
 * <p>partnerChain 是全站第一個真正的業務端點 permitAll() chain——ECPay 的 server-to-server callback
 * 沒有 JWT 可驗（ECPay 不是這個系統的使用者/服務主體），安全性靠 Controller 內自己算
 * {@link com.tengan.mall.payment.infrastructure.ecpay.EcpayCheckMacValueCalculator} 驗證
 * CheckMacValue 簽章，不靠 Spring Security 的認證機制。</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/actuator/**").csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain partnerChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/partner/payments/**").csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain customerChain(HttpSecurity http, JwtDecoder userJwtDecoder) throws Exception {
        http.securityMatcher("/api/customer/**").csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(userJwtDecoder)));
        return http.build();
    }

    /** 驗證 tengan-auth 簽發的 Service JWT，各端點的 scope 要求標在 Controller 方法的 @PreAuthorize 上。 */
    @Bean
    @Order(4)
    public SecurityFilterChain internalChain(HttpSecurity http, JwtDecoder serviceJwtDecoder) throws Exception {
        http.securityMatcher("/internal/**").csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(serviceJwtDecoder)));
        return http.build();
    }
}
