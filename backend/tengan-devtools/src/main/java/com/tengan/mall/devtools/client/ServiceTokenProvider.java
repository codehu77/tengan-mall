package com.tengan.mall.devtools.client;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

/** 比照 tengan-admin 的 ProductServiceTokenProvider/MediaServiceTokenProvider，一個 provider 服務兩個
 * registrationId（各自 scope 不同，見 application.yml），principal 名稱沿用官方範例慣例直接用
 * registrationId 本身——client_credentials 沒有真實使用者主體。 */
@Component
public class ServiceTokenProvider {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public ServiceTokenProvider(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    public String getAccessToken(String registrationId) {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId(registrationId)
                .principal(registrationId)
                .build();
        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(request);
        if (authorizedClient == null) {
            throw new IllegalStateException("無法取得呼叫 " + registrationId + " 用的 Service JWT");
        }
        return authorizedClient.getAccessToken().getTokenValue();
    }
}
