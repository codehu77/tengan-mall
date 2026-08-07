package com.tengan.mall.auth.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 四條鏈（docs/JWT設計.md 第四節）：
 * 1. public + jwks + actuator：完全放行
 * 2. /api/customer/auth/refresh：故意也放行——這支端點驗證的是 refresh token
 *    （opaque string，存在 cookie/request body），不是 access token JWT，不能套用
 *    第 3 條鏈「強制持有效 access token」的規則，否則 access token 過期後永遠換不到新的
 *    （這正是 refresh 存在的目的），會變成無法自我修復的死結。
 * 3. 其餘 /api/customer/**：強制持有效 access token，下游驗簽解出 userId。
 * 4. /internal/**：驗證 Service JWT（tengan-admin 呼叫 disable/enable account 用）——
 *    tengan-auth 這裡比較特別，驗證自己簽發的 Service JWT，jwk-set-uri 指向自己的
 *    /oauth2/service/jwks，見 tengan-mall-auth.yaml 的 tengan.jwt.service 設定。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/public/**", "/oauth2/jwks", "/actuator/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain refreshChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/customer/auth/refresh")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain customerChain(HttpSecurity http, JwtDecoder userJwtDecoder) throws Exception {
        http.securityMatcher("/api/customer/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(userJwtDecoder)));
        return http.build();
    }

    @Bean
    @Order(4)
    public SecurityFilterChain internalChain(HttpSecurity http, JwtDecoder serviceJwtDecoder) throws Exception {
        http.securityMatcher("/internal/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(serviceJwtDecoder)));
        return http.build();
    }
}
