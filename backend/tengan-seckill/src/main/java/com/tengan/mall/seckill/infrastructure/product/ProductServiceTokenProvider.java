package com.tengan.mall.seckill.infrastructure.product;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

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
