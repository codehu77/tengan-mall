package com.tengan.mall.order.infrastructure.seckill;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

@Component
public class SeckillServiceTokenProvider {

    private static final String CLIENT_REGISTRATION_ID = "tengan-seckill";

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public SeckillServiceTokenProvider(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    public String getAccessToken() {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId(CLIENT_REGISTRATION_ID)
                .principal(CLIENT_REGISTRATION_ID)
                .build();
        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(request);
        if (authorizedClient == null) {
            throw new IllegalStateException("無法取得呼叫 tengan-seckill 用的 Service JWT");
        }
        return authorizedClient.getAccessToken().getTokenValue();
    }
}
