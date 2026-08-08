package com.tengan.mall.cart.infrastructure.product;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

/**
 * tengan-cart 呼叫 tengan-product 的 /internal/** 端點要帶 Service JWT——比照 tengan-admin
 * 呼叫 tengan-product 的既有模式（AuthorizedClientServiceOAuth2AuthorizedClientManager，
 * client_credentials 是服務對服務授權，不綁定任何使用者請求）。client 註冊資訊在 Nacos
 * tengan-mall-cart.yaml 的 spring.security.oauth2.client.registration.tengan-product。
 */
@Configuration
public class ProductClientConfig {

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {
        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        manager.setAuthorizedClientProvider(authorizedClientProvider);
        return manager;
    }

    /**
     * SkuDetailDto 只取 tengan-product 回應裡購物車用得到的欄位子集，自訂 ObjectMapper 關掉
     * FAIL_ON_UNKNOWN_PROPERTIES，不依賴 Spring Boot 自動配置的全域 ObjectMapper 預設值。
     */
    @Bean
    public RestClient productRestClient(@Value("${tengan.product.base-url}") String baseUrl) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return RestClient.builder()
                .baseUrl(baseUrl)
                .messageConverters(converters -> converters.add(0,
                        new MappingJackson2HttpMessageConverter(objectMapper)))
                .build();
    }
}
