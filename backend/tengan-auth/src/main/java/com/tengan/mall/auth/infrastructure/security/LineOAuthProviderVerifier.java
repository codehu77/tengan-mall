package com.tengan.mall.auth.infrastructure.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tengan.mall.auth.application.port.OAuthUserInfo;
import com.tengan.mall.auth.domain.exception.InvalidOAuthTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * LINE 登入用 authorization code 驗證。跟 Google/Facebook 不同，LINE 沒有 JS SDK 能在頁面內直接拿到
 * 已簽章的憑證，前端整頁導去 LINE 授權頁換回來的只有一次性的 {@code code}，這裡要先用
 * {@code client_secret} 對 LINE token endpoint 做 server-to-server 交換，換到的 {@code id_token}
 * （OIDC JWT）才是真正要驗證、解析使用者資料的憑證。
 *
 * <p><b>id_token 驗證刻意不走本機 JWKS 解碼（{@code JwtDecoder}），改打 LINE 官方的
 * {@code /oauth2/v2.1/verify} endpoint</b>——原本仿 {@code GoogleOAuthProviderVerifier} 用
 * {@code JwtDecoders.fromIssuerLocation} 建的 JwtDecoder 實測會丟
 * 「Signed JWT rejected: Another algorithm expected, or no matching key(s) found」，LINE 的
 * id_token 簽章方式跟 Google 走的標準 RS256+JWKS 模式對不上。改打 LINE 的 verify endpoint（帶
 * {@code id_token}/{@code client_id}/{@code nonce}）讓 LINE 自己驗簽章+aud+nonce，直接回傳解碼後的
 * claim JSON，完全繞開本機需要正確猜中簽章演算法/金鑰格式這個問題，也是 LINE 官方文件推薦的驗證方式
 * 之一。
 *
 * <p><b>刻意不 implements {@link com.tengan.mall.auth.application.port.OAuthProviderVerifier}</b>
 * ——那個介面是單參數 {@code verify(String token)}，Google/Facebook 從沒被當作可互換的同一種型別
 * 動態分派（{@code GoogleLoginService}/{@code FacebookLoginService} 都是建構子直接注入具名的具體
 * 驗證器），共用介面只是文件性質的約定，不是必須遵守的多型契約。LINE 這裡多需要一個 {@code nonce}
 * 驗證 id_token 沒被冒用/重放，若硬塞進共用介面會逼 Google/Facebook 的 verify() 也跟著多帶一個自己
 * 用不到的參數，因此獨立成自己的兩參數簽章，{@code LineLoginService}/{@code LinkLineAccountService}
 * 直接注入這個具體型別。
 */
@Component
public class LineOAuthProviderVerifier {

    private final String channelId;
    private final String channelSecret;
    private final String redirectUri;
    private final RestClient restClient = RestClient.builder().baseUrl("https://api.line.me").build();

    public LineOAuthProviderVerifier(@Value("${line.oauth.channel-id}") String channelId,
            @Value("${line.oauth.channel-secret}") String channelSecret,
            @Value("${line.oauth.redirect-uri}") String redirectUri) {
        this.channelId = channelId;
        this.channelSecret = channelSecret;
        this.redirectUri = redirectUri;
    }

    public OAuthUserInfo verify(String code, String nonce) {
        String idToken = exchangeCodeForIdToken(code);
        VerifyResponse claims = verifyIdToken(idToken, nonce);

        boolean emailVerified = claims.email() != null && !claims.email().isBlank();
        return new OAuthUserInfo(claims.sub(), claims.email(), emailVerified, claims.name(), claims.picture());
    }

    private String exchangeCodeForIdToken(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", redirectUri);
        form.add("client_id", channelId);
        form.add("client_secret", channelSecret);

        TokenResponse response;
        try {
            response = restClient.post()
                    .uri("/oauth2/v2.1/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TokenResponse.class);
        } catch (RestClientException e) {
            throw new InvalidOAuthTokenException(e.getMessage());
        }
        if (response == null || response.idToken() == null) {
            throw new InvalidOAuthTokenException("LINE token endpoint 未回傳有效 id_token");
        }
        return response.idToken();
    }

    /** LINE 這支 endpoint 會驗簽章+aud（client_id）+nonce（有帶就會比對），任一項不符會回非 2xx。 */
    private VerifyResponse verifyIdToken(String idToken, String nonce) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("id_token", idToken);
        form.add("client_id", channelId);
        form.add("nonce", nonce);

        VerifyResponse response;
        try {
            response = restClient.post()
                    .uri("/oauth2/v2.1/verify")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(VerifyResponse.class);
        } catch (RestClientException e) {
            throw new InvalidOAuthTokenException(e.getMessage());
        }
        if (response == null || response.sub() == null) {
            throw new InvalidOAuthTokenException("LINE id_token 驗證失敗");
        }
        return response;
    }

    private record TokenResponse(@JsonProperty("id_token") String idToken) {
    }

    private record VerifyResponse(String sub, String name, String picture, String email) {
    }
}
