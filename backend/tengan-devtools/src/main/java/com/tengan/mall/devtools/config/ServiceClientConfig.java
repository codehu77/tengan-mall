package com.tengan.mall.devtools.config;

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
 * 完全比照 tengan-admin 呼叫 internal API 的既有模式（見 ProductClientConfig/MediaClientConfig）：
 * client_credentials 用官方 spring-security-oauth2-client 的
 * AuthorizedClientServiceOAuth2AuthorizedClientManager（service 級，不綁 HttpServletRequest）。
 * 差別只在於這裡 client 註冊資訊直接寫在自己的 application.yml，不放 Nacos——這是一次性本機
 * 小工具，不值得為它去動 Nacos 主控台設定。
 */
@Configuration
public class ServiceClientConfig {

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

    @Bean
    public RestClient mediaRestClient(@Value("${tengan.media.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient inventoryRestClient(@Value("${tengan.inventory.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    /** 不走 oauth2 client（client_credentials 沒有真人主體），純粹呼叫 tengan-admin 登入端點換 admin JWT，
     * 見 AdminBotTokenProvider。 */
    @Bean
    public RestClient adminRestClient(@Value("${tengan.admin.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
