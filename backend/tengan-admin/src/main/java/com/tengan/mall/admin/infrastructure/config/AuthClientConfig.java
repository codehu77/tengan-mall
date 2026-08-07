package com.tengan.mall.admin.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * 呼叫 tengan-auth 的 /internal/accounts（停權會員時同步停用登入帳號）——共用 ProductClientConfig
 * 那顆 authorizedClientManager bean，只多開一個 RestClient。client 註冊資訊在 Nacos
 * tengan-mall-admin.yaml 的 spring.security.oauth2.client.registration.tengan-auth。
 */
@Configuration
public class AuthClientConfig {

    @Bean
    public RestClient authRestClient(@Value("${tengan.auth.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
