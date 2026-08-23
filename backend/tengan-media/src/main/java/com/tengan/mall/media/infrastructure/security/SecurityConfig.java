package com.tengan.mall.media.infrastructure.security;

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
 * 三條 chain：public（首頁 Banner 展示，不需登入）+ customer（顧客頭像上傳，zero-trust 用
 * userJwtDecoder 驗證）+ internal（供 tengan-admin 呼叫的 Banner CRUD + 後台素材上傳，用
 * serviceJwtDecoder 驗證，scope 要求標在 Controller 方法的 @PreAuthorize）。沒有人用 admin token
 * 直接打這個服務（admin 端一律透過 tengan-admin BFF 轉發），所以不需要 admin JWT decoder。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/public/**", "/actuator/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain customerChain(HttpSecurity http, JwtDecoder userJwtDecoder) throws Exception {
        http.securityMatcher("/api/customer/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(userJwtDecoder)));
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain internalChain(HttpSecurity http, JwtDecoder serviceJwtDecoder) throws Exception {
        http.securityMatcher("/internal/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(serviceJwtDecoder)));
        return http.build();
    }
}
