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
 * 啟動時確保 tengan-admin 這個 client 存在（冪等：先查 findByClientId 再決定要不要建）。
 * 不用 SQL migration 手刻——client_settings/token_settings 兩欄是 Spring Security 內部 JSON
 * 序列化格式，用官方 builder + repository.save() 讓函式庫自己序列化，不要自己猜格式。
 *
 * <p>scope 目前只給 product.read/product.write（tengan-product 是唯一有 internal 端點的服務），
 * 之後每加一個服務的 internal 端點，就幫這個 client 多加一組 scope，不用這次就把後台清單裡
 * 列的全部 scope 一次註冊完（沒有服務在檢查的 scope 現在給了也驗證不了）。</p>
 */
@Component
public class RegisteredClientSeeder implements ApplicationRunner {

    private static final String ADMIN_CLIENT_ID = "tengan-admin";

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminClientSecret;

    public RegisteredClientSeeder(RegisteredClientRepository registeredClientRepository,
            PasswordEncoder passwordEncoder,
            @Value("${tengan.oauth2.admin-client-secret:tengan-admin-secret}") String adminClientSecret) {
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminClientSecret = adminClientSecret;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (registeredClientRepository.findByClientId(ADMIN_CLIENT_ID) != null) {
            return;
        }
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(ADMIN_CLIENT_ID)
                .clientIdIssuedAt(Instant.now())
                .clientSecret(passwordEncoder.encode(adminClientSecret))
                .clientName(ADMIN_CLIENT_ID)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("product.read")
                .scope("product.write")
                .clientSettings(ClientSettings.builder().build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenTimeToLive(Duration.ofMinutes(5))
                        .build())
                .build();
        registeredClientRepository.save(client);
    }
}
