package com.tengan.mall.admin.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

/**
 * `tengan-admin` 呼叫 `tengan-product` 的 `/internal/**` 端點要帶 Service JWT——用官方
 * `spring-security-oauth2-client` 的 Client Credentials 支援（`AuthorizedClientServiceOAuth2AuthorizedClientManager`
 * 內建 token 快取/接近過期自動換發），不手刻 WebClient+快取邏輯（見 backend_dev_plan.md 這次的技術選擇）。
 * client 註冊資訊在 Nacos `tengan-mall-admin.yaml` 的 `spring.security.oauth2.client.registration.tengan-product`。
 *
 * <p>這裡用 {@code AuthorizedClientServiceOAuth2AuthorizedClientManager}（service 級）不是
 * {@code DefaultOAuth2AuthorizedClientManager}（request 級）——client_credentials 是服務對服務的
 * 授權，不綁定任何使用者請求，沒有 {@code HttpServletRequest}/{@code Authentication} 可用。</p>
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

    @Bean
    public RestClient productRestClient(@Value("${tengan.product.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
