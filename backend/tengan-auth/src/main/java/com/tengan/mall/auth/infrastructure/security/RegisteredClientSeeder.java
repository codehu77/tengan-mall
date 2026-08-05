package com.tengan.mall.auth.infrastructure.security;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

/**
 * 啟動時確保 tengan-admin/tengan-search 這兩個 client 存在（冪等：先查 findByClientId 再決定要不要建）。
 * 不用 SQL migration 手刻——client_settings/token_settings 兩欄是 Spring Security 內部 JSON
 * 序列化格式，用官方 builder + repository.save() 讓函式庫自己序列化，不要自己猜格式。
 *
 * <p>scope 只給每個 client 實際會用到的：tengan-admin 需要 product.read/write（呼叫 tengan-product）
 * + search.write（觸發 tengan-search 重建索引）；tengan-search 只需要 product.read（拉全量匯出資料，
 * 不會寫 tengan-product 任何東西）。之後每加一個服務的 internal 端點，就幫需要呼叫它的 client
 * 多加一組 scope，不用這次就把後台清單裡列的全部 scope 一次註冊完。</p>
 */
@Component
public class RegisteredClientSeeder implements ApplicationRunner {

    private static final String ADMIN_CLIENT_ID = "tengan-admin";
    private static final String SEARCH_CLIENT_ID = "tengan-search";

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminClientSecret;
    private final String searchClientSecret;

    public RegisteredClientSeeder(RegisteredClientRepository registeredClientRepository,
            PasswordEncoder passwordEncoder,
            @Value("${tengan.oauth2.admin-client-secret:tengan-admin-secret}") String adminClientSecret,
            @Value("${tengan.oauth2.search-client-secret:tengan-search-secret}") String searchClientSecret) {
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminClientSecret = adminClientSecret;
        this.searchClientSecret = searchClientSecret;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedIfAbsent(ADMIN_CLIENT_ID, adminClientSecret, "product.read", "product.write", "search.write");
        seedIfAbsent(SEARCH_CLIENT_ID, searchClientSecret, "product.read");
    }

    /**
     * 既有 client（例如舊的 tengan-admin）之前只註冊過 product.read/write，這次多了 search.write——
     * 不是單純「不存在才建」，存在但少 scope 時也要補上，不然舊資料庫的 client 永遠停留在建立當下
     * 的 scope 集合，之後每次加 scope 都要手動去 DB 改。
     */
    private void seedIfAbsent(String clientId, String clientSecret, String... scopes) {
        RegisteredClient existing = registeredClientRepository.findByClientId(clientId);
        if (existing != null) {
            java.util.Set<String> missing = new java.util.LinkedHashSet<>(java.util.List.of(scopes));
            missing.removeAll(existing.getScopes());
            if (!missing.isEmpty()) {
                RegisteredClient.Builder builder = RegisteredClient.from(existing);
                missing.forEach(builder::scope);
                registeredClientRepository.save(builder.build());
            }
            return;
        }
        RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientIdIssuedAt(Instant.now())
                .clientSecret(passwordEncoder.encode(clientSecret))
                .clientName(clientId)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientSettings(ClientSettings.builder().build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenTimeToLive(Duration.ofMinutes(5))
                        .build());
        for (String scope : scopes) {
            builder.scope(scope);
        }
        registeredClientRepository.save(builder.build());
    }
}
