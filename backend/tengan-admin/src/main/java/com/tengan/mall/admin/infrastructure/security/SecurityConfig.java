package com.tengan.mall.admin.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 三條鏈，比照 tengan-auth 的 SecurityConfig，tier 從 /api/customer/** 換成 /api/admin/**：
 * 1. public + jwks + actuator：完全放行。
 * 2. /api/admin/auth/login、/api/admin/auth/refresh：也放行——這兩支端點的本質都是
 *    「還沒有（或已過期）有效 access token」的情境（登入前本來就沒有 token，refresh 驗證的是
 *    opaque refresh token 不是 access token JWT），不能套用第 3 條鏈「強制持有效 access token」
 *    的規則，否則登入本身或 access token 過期後的換發都會變成無法自我修復的死結。
 *    Gateway 的 UserJwtFilter 也要有對應的放行例外，兩邊要保持一致。
 * 3. 其餘 /api/admin/**：強制持有效 access token，下游驗簽解出 adminId + permissions。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/oauth2/jwks", "/actuator/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain unauthenticatedChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/admin/auth/login", "/api/admin/auth/refresh")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain adminChain(HttpSecurity http, JwtDecoder userJwtDecoder,
            AdminJwtAuthenticationConverter adminJwtAuthenticationConverter,
            AuditingAccessDeniedHandler auditingAccessDeniedHandler) throws Exception {
        http.securityMatcher("/api/admin/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(userJwtDecoder)
                        .jwtAuthenticationConverter(adminJwtAuthenticationConverter)))
                .exceptionHandling(exception -> exception.accessDeniedHandler(auditingAccessDeniedHandler));
        return http.build();
    }
}
