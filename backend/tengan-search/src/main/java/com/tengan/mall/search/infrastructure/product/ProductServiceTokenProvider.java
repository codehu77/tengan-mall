package com.tengan.mall.search.infrastructure.product;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

/** 換發/快取呼叫 tengan-product 用的 Service JWT，原封不動搬用 tengan-admin 的同名類別模式。 */
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
