package com.tengan.mall.admin.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * 呼叫 tengan-seckill 用的 RestClient——client_credentials 授權共用既有 authorizedClientManager
 * bean（跟 CouponClientConfig 同一顆）。client 註冊資訊在 Nacos tengan-mall-admin.yaml 的
 * spring.security.oauth2.client.registration.tengan-seckill。
 */
@Configuration
public class SeckillClientConfig {

    @Bean
    public RestClient seckillRestClient(@Value("${tengan.seckill.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
