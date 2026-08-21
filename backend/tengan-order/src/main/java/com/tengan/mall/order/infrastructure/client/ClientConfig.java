package com.tengan.mall.order.infrastructure.client;

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
 * tengan-order 呼叫 cart/product/inventory/coupon/wallet/seckill 這幾個服務的 /internal/** 端點
 * 都要帶 Service JWT——比照 tengan-admin/tengan-cart 呼叫其他服務的既有模式
 * （AuthorizedClientServiceOAuth2AuthorizedClientManager，client_credentials 是服務對服務授權，
 * 不綁定任何使用者請求）。所有 client 共用同一顆 authorizedClientManager，各自的 client 註冊資訊
 * 在 Nacos tengan-mall-order.yaml。
 */
@Configuration
public class ClientConfig {

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

    /** 回應 DTO 只取用得到的欄位子集，關掉 FAIL_ON_UNKNOWN_PROPERTIES（比照 tengan-cart 呼叫 product 的既有模式）。 */
    private RestClient buildRestClient(String baseUrl) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return RestClient.builder()
                .baseUrl(baseUrl)
                .messageConverters(converters -> converters.add(0,
                        new MappingJackson2HttpMessageConverter(objectMapper)))
                .build();
    }

    @Bean
    public RestClient cartRestClient(@Value("${tengan.cart.base-url}") String baseUrl) {
        return buildRestClient(baseUrl);
    }

    @Bean
    public RestClient productRestClient(@Value("${tengan.product.base-url}") String baseUrl) {
        return buildRestClient(baseUrl);
    }

    @Bean
    public RestClient inventoryRestClient(@Value("${tengan.inventory.base-url}") String baseUrl) {
        return buildRestClient(baseUrl);
    }

    @Bean
    public RestClient couponRestClient(@Value("${tengan.coupon.base-url}") String baseUrl) {
        return buildRestClient(baseUrl);
    }

    @Bean
    public RestClient walletRestClient(@Value("${tengan.wallet.base-url}") String baseUrl) {
        return buildRestClient(baseUrl);
    }

    @Bean
    public RestClient seckillRestClient(@Value("${tengan.seckill.base-url}") String baseUrl) {
        return buildRestClient(baseUrl);
    }
}
