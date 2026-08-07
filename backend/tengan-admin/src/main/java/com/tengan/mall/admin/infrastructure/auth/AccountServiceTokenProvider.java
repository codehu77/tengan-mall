package com.tengan.mall.admin.infrastructure.auth;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

/** 換發/快取呼叫 tengan-auth 用的 Service JWT，比照 ProductServiceTokenProvider 的既有模式。 */
@Component
public class AccountServiceTokenProvider {

    private static final String CLIENT_REGISTRATION_ID = "tengan-auth";

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public AccountServiceTokenProvider(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    public String getAccessToken() {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId(CLIENT_REGISTRATION_ID)
                .principal(CLIENT_REGISTRATION_ID)
                .build();
        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(request);
        if (authorizedClient == null) {
            throw new IllegalStateException("無法取得呼叫 tengan-auth 用的 Service JWT");
        }
        return authorizedClient.getAccessToken().getTokenValue();
    }
}
