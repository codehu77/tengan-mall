package com.tengan.mall.devtools.client;

import com.tengan.mall.devtools.client.dto.AdminLoginRequest;
import com.tengan.mall.devtools.client.dto.AdminLoginResponse;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 呼叫建庫存這種標記真人操作者的 internal 端點時，X-Identity-Assertion 一定要是 tengan-admin
 * 簽發的 admin JWT（見 tengan-inventory 的 adminIdentityAssertionVerifier，不接受 service JWT
 * 冒充）。devtools 沒有真人登入，所以用一個專屬的 bot 管理員帳號登入 tengan-admin 換 admin JWT，
 * 稽核紀錄（InventoryOperLog）operator 就會是這個帳號的 username，清楚可追溯是自動匯入寫入的。
 *
 * <p>tengan-admin 的 access token TTL 固定 15 分鐘（見 AdminAccessTokenIssuerAdapter），這裡快取
 * 到剩 1 分鐘就重新登入，避免每次寫庫存都登入一次、灌爆 refresh_token 表。</p>
 */
@Component
public class AdminBotTokenProvider {

    private final RestClient adminRestClient;
    private final String botUsername;
    private final String botPassword;

    private volatile String cachedToken;
    private volatile Instant cachedTokenExpiresAt = Instant.EPOCH;

    public AdminBotTokenProvider(RestClient adminRestClient,
            @Value("${tengan.admin.bot-username}") String botUsername,
            @Value("${tengan.admin.bot-password}") String botPassword) {
        this.adminRestClient = adminRestClient;
        this.botUsername = botUsername;
        this.botPassword = botPassword;
    }

    public synchronized String getIdentityAssertion() {
        if (cachedToken == null || Instant.now().isAfter(cachedTokenExpiresAt)) {
            AdminLoginResponse response = adminRestClient.post()
                    .uri("/api/admin/auth/login")
                    .body(new AdminLoginRequest(botUsername, botPassword))
                    .retrieve()
                    .body(AdminLoginResponse.class);
            if (response == null || response.accessToken() == null) {
                throw new IllegalStateException("devtools bot 帳號登入 tengan-admin 失敗，拿不到 admin JWT");
            }
            cachedToken = response.accessToken();
            cachedTokenExpiresAt = Instant.now().plusSeconds(14 * 60);
        }
        return "Bearer " + cachedToken;
    }
}
