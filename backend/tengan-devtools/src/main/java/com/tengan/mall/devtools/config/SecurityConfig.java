package com.tengan.mall.devtools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 只要 classpath 上有 spring-boot-starter-oauth2-client + 註冊了 ClientRegistration，Spring Boot
 * 沒看到自訂 SecurityFilterChain 時就會套用預設安全設定，把這些 registration（其實是給
 * client_credentials 對外呼叫用的）誤當成「Login with OAuth 2.0」的登入選項生一個登入頁出來。
 * 這支工具只在本機用、完全不需要任何登入驗證，明確蓋掉預設行為，全部放行。
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
