package com.tengan.mall.auth.infrastructure.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tengan.mall.auth.application.port.OAuthProviderVerifier;
import com.tengan.mall.auth.application.port.OAuthUserInfo;
import com.tengan.mall.auth.domain.exception.InvalidOAuthTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Facebook 登入用 access token 驗證（{@code FB.login()} 拿到的 user access token）。跟 Google GIS
 * 的 JWT 不同，這顆 token 不是自我驗證的簽章 JWT，要打兩支 Graph API：先用 app access token
 * （{@code {app-id}|{app-secret}}）呼叫 {@code /debug_token} 確認這顆 token 是「本應用程式」核發
 * （對齊 {@link GoogleOAuthConfig} 的 aud claim 檢查，避免拿別的 Facebook App 核發的 token
 * 冒用登入），再用該 token 本身呼叫 {@code /me} 取正規化後要用的個人資料。
 */
@Component
public class FacebookOAuthProviderVerifier implements OAuthProviderVerifier {

    private final String appId;
    private final String appAccessToken;
    private final RestClient restClient = RestClient.create("https://graph.facebook.com/v26.0");

    public FacebookOAuthProviderVerifier(@Value("${facebook.oauth.app-id}") String appId,
            @Value("${facebook.oauth.app-secret}") String appSecret) {
        this.appId = appId;
        this.appAccessToken = appId + "|" + appSecret;
    }

    @Override
    public OAuthUserInfo verify(String accessToken) {
        assertIssuedForThisApp(accessToken);

        FacebookProfile profile;
        try {
            profile = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/me")
                            .queryParam("fields", "id,name,email,picture")
                            .queryParam("access_token", accessToken)
                            .build())
                    .retrieve()
                    .body(FacebookProfile.class);
        } catch (RestClientException e) {
            throw new InvalidOAuthTokenException(e.getMessage());
        }
        if (profile == null || profile.id() == null) {
            throw new InvalidOAuthTokenException("Graph API 未回傳有效使用者資料");
        }

        boolean emailVerified = profile.email() != null && !profile.email().isBlank();
        String pictureUrl = profile.picture() != null && profile.picture().data() != null
                ? profile.picture().data().url() : null;
        return new OAuthUserInfo(profile.id(), profile.email(), emailVerified, profile.name(), pictureUrl);
    }

    private void assertIssuedForThisApp(String accessToken) {
        DebugTokenResponse debug;
        try {
            debug = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/debug_token")
                            .queryParam("input_token", accessToken)
                            .queryParam("access_token", appAccessToken)
                            .build())
                    .retrieve()
                    .body(DebugTokenResponse.class);
        } catch (RestClientException e) {
            throw new InvalidOAuthTokenException(e.getMessage());
        }
        DebugTokenData data = debug != null ? debug.data() : null;
        if (data == null || !data.isValid() || !appId.equals(data.appId())) {
            throw new InvalidOAuthTokenException("access token 無效或非本應用程式核發");
        }
    }

    private record DebugTokenResponse(DebugTokenData data) {
    }

    private record DebugTokenData(@JsonProperty("app_id") String appId, @JsonProperty("is_valid") boolean isValid) {
    }

    private record FacebookProfile(String id, String name, String email, Picture picture) {
    }

    private record Picture(PictureData data) {
    }

    private record PictureData(String url) {
    }
}
