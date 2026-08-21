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
 * + search.write（觸發 tengan-search 重建索引）+ member.read（呼叫 tengan-member，member 只有
 * 唯讀端點，沒有 write scope）+ account.read/write（呼叫 tengan-auth 自己的 /internal/accounts，
 * 停權會員時同步停用登入帳號、後台列表即時組裝顯示狀態）；tengan-search 只需要 product.read
 * （拉全量匯出資料，不會寫 tengan-product 任何東西）；tengan-cart 只需要 product.read
 * （購物車即時查價，呼叫新增的 /internal/products/skus 批次端點，不寫 tengan-product 任何東西）。
 * tengan-admin 這次（Phase 5）多了 inventory.read/write（呼叫 tengan-inventory 的庫存/倉庫/工作單
 * 管理端點）+ coupon.read/write（呼叫 tengan-coupon 的模板 CRUD/核發端點）。Phase 6 再多了
 * order.read/write（呼叫 tengan-order 的訂單管理端點）；同時新增 tengan-order 這個 client 本身
 * ——下單編排需要呼叫 cart（讀取已勾選項目+清空）/product（即時查價）/inventory（鎖庫存）/
 * coupon（驗證+核銷+回滾）四個服務，scope 只給實際會用到的（cart.read/write、product.read、
 * inventory.write、coupon.read/write，沒有 inventory.read/coupon.write 用不到的 write/read 組合）。
 * 之後每加一個服務的 internal 端點，就幫需要呼叫它的 client 多加一組 scope，不用這次就把後台清單裡
 * 列的全部 scope 一次註冊完。Phase 8 新增 tengan-wallet 網域：`tengan-wallet` 本身不主動呼叫任何服務
 * （純 Resource Server），所以不用新增它自己的 client，只需要幫會呼叫它的 tengan-order（下單 Saga 核銷
 * 點數/確認收貨 reserve/排程 earn/取消 revert）跟 tengan-admin（行銷管理頁）補 wallet.read/wallet.write。
 * Phase 9 新增 tengan-seckill 網域：`tengan-seckill` 會主動呼叫 tengan-inventory 的結算端點，所以
 * 新增它自己的 client（只給 inventory.seckill.write，不是一般的 inventory.write——秒殺結算跟一般
 * 訂單扣庫存刻意用不同 scope 隔開，見微服務前台API待開發清單.md「服務間身份驗證」章節）；同時幫
 * 會呼叫 tengan-seckill 的 tengan-order（訂單建立 Saga 查詢/保留/釋放配額）跟 tengan-admin（秒殺
 * 活動管理頁）補 seckill.read/seckill.write。Phase 9 前台整合再補一筆：`tengan-seckill` 自己的公開
 * 展示端點（`GET /api/public/seckill/activities`）要補商品名稱/圖片，多給它 product.read 呼叫
 * tengan-product。</p>
 */
@Component
public class RegisteredClientSeeder implements ApplicationRunner {

    private static final String ADMIN_CLIENT_ID = "tengan-admin";
    private static final String SEARCH_CLIENT_ID = "tengan-search";
    private static final String CART_CLIENT_ID = "tengan-cart";
    private static final String ORDER_CLIENT_ID = "tengan-order";
    private static final String PAYMENT_CLIENT_ID = "tengan-payment";
    private static final String SECKILL_CLIENT_ID = "tengan-seckill";

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminClientSecret;
    private final String searchClientSecret;
    private final String cartClientSecret;
    private final String orderClientSecret;
    private final String paymentClientSecret;
    private final String seckillClientSecret;

    public RegisteredClientSeeder(RegisteredClientRepository registeredClientRepository,
            PasswordEncoder passwordEncoder,
            @Value("${tengan.oauth2.admin-client-secret:tengan-admin-secret}") String adminClientSecret,
            @Value("${tengan.oauth2.search-client-secret:tengan-search-secret}") String searchClientSecret,
            @Value("${tengan.oauth2.cart-client-secret:tengan-cart-secret}") String cartClientSecret,
            @Value("${tengan.oauth2.order-client-secret:tengan-order-secret}") String orderClientSecret,
            @Value("${tengan.oauth2.payment-client-secret:tengan-payment-secret}") String paymentClientSecret,
            @Value("${tengan.oauth2.seckill-client-secret:tengan-seckill-secret}") String seckillClientSecret) {
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminClientSecret = adminClientSecret;
        this.searchClientSecret = searchClientSecret;
        this.cartClientSecret = cartClientSecret;
        this.orderClientSecret = orderClientSecret;
        this.paymentClientSecret = paymentClientSecret;
        this.seckillClientSecret = seckillClientSecret;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedIfAbsent(ADMIN_CLIENT_ID, adminClientSecret, "product.read", "product.write", "search.write",
                "member.read", "account.read", "account.write", "inventory.read", "inventory.write", "coupon.read",
                "coupon.write", "order.read", "order.write", "payment.read", "payment.write", "wallet.read",
                "wallet.write", "seckill.read", "seckill.write");
        seedIfAbsent(SEARCH_CLIENT_ID, searchClientSecret, "product.read");
        seedIfAbsent(CART_CLIENT_ID, cartClientSecret, "product.read");
        seedIfAbsent(ORDER_CLIENT_ID, orderClientSecret, "cart.read", "cart.write", "product.read",
                "inventory.write", "coupon.read", "coupon.write", "wallet.read", "wallet.write", "seckill.read",
                "seckill.write");
        seedIfAbsent(PAYMENT_CLIENT_ID, paymentClientSecret, "order.read", "order.write", "wallet.write");
        seedIfAbsent(SECKILL_CLIENT_ID, seckillClientSecret, "inventory.seckill.write", "product.read");
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
