package com.tengan.mall.admin.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * 呼叫 tengan-order 用的 RestClient——client_credentials 授權共用 ProductClientConfig 那顆
 * authorizedClientManager bean。client 註冊資訊在 Nacos tengan-mall-admin.yaml 的
 * spring.security.oauth2.client.registration.tengan-order。
 */
@Configuration
public class OrderClientConfig {

    @Bean
    public RestClient orderRestClient(@Value("${tengan.order.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
