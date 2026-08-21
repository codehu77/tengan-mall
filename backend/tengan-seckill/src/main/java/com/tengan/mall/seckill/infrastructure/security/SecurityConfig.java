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
 * internal chain（供 tengan-order/tengan-admin 呼叫）+ public chain（`/api/public/seckill/**`，
 * 首頁輪播/獨立列表頁/商品詳情頁徽章/購物車顯示共用的展示端點，不需要登入）+ actuator。
 * 目前設計裡秒殺沒有獨立的 `/api/customer/**` 端點——加入購物車/提交訂單都是既有
 * tengan-cart/tengan-order 的端點，保留配額整個是服務對服務呼叫，瀏覽器不會直接打
 * tengan-seckill 的任何寫入端點，所以不需要 customer chain。
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
