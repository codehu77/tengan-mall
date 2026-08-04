package com.tengan.mall.admin.infrastructure.product;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

/**
 * 換發/快取呼叫 tengan-product 用的 Service JWT。client_credentials 沒有使用者主體，principal
 * 名稱照 Spring Security 官方範例慣例直接用 clientRegistrationId 本身（"tengan-product"）——
 * 這個字串只是 {@code OAuth2AuthorizedClientService} 內部快取的 key，不代表任何真實使用者。
 */
@Component
public class ProductServiceTokenProvider {

    private static final String CLIENT_REGISTRATION_ID = "tengan-product";

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public ProductServiceTokenProvider(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    public String getAccessToken() {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId(CLIENT_REGISTRATION_ID)
                .principal(CLIENT_REGISTRATION_ID)
                .build();
        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(request);
        if (authorizedClient == null) {
            throw new IllegalStateException("無法取得呼叫 tengan-product 用的 Service JWT");
        }
        return authorizedClient.getAccessToken().getTokenValue();
    }
}
