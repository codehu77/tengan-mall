package com.tengan.mall.seckill.infrastructure.security;

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
 * 這次只先建 internal chain（供 tengan-order/tengan-admin 呼叫）+ actuator 公開端點。
 * `/api/public/**`（場次列表/單品詳情）跟 `/api/customer/**`（目前設計裡秒殺沒有獨立的
 * customer 端點，加入購物車/提交訂單都是既有 tengan-cart/tengan-order 的端點）留到之後
 * 真的要做前台展示頁時再加，不用先佔位一個沒有對應 controller 的 chain。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/actuator/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /** 驗證 tengan-auth 簽發的 Service JWT，各端點的 scope 要求標在 Controller 方法的 @PreAuthorize 上。 */
    @Bean
    @Order(2)
    public SecurityFilterChain internalChain(HttpSecurity http, JwtDecoder serviceJwtDecoder) throws Exception {
        http.securityMatcher("/internal/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(serviceJwtDecoder)));
        return http.build();
    }
}
