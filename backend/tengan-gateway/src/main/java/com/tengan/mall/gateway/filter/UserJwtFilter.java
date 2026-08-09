package com.tengan.mall.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Gateway 唯一的身份快速過濾器（docs/JWT設計.md 第一、二節）：只驗簽章 + exp，
 * 通過後原樣轉發 Authorization header，不解析 claims、不注入 X-User-Id/X-Admin-Id
 * 這類明文身份 header——下游服務各自對同一顆 token 重新驗簽解出 userId/adminId。
 *
 * <p>依路徑分四種處理（/internal/** 不在這裡處理，靠 Gateway 路由表完全不設該前綴的
 * 路由條目來結構性阻擋，見 docs/JWT設計.md 第二節）：
 * <ul>
 *   <li>{@code /api/public/**}、{@code /api/partner/**}：不做任何 JWT 處理，直接放行
 *       （partner 端點走簽章戳記或 Service JWT，跟使用者 JWT 機制無關）</li>
 *   <li>{@code /api/customer/auth/refresh}、{@code /api/admin/auth/login}、
 *       {@code /api/admin/auth/refresh}：也直接放行——這些端點的本質都是「還沒有（或已過期）
 *       有效 access token」的情境（登入前本來就沒有 token，refresh 驗證的是 opaque refresh
 *       token 不是 access token JWT），不能套用「強制持有效 access token」的規則，否則會變成
 *       無法自我修復的死結（tengan-auth/tengan-admin 自己的 SecurityConfig 也有對應的放行鏈，
 *       兩邊要一致，不能只修一邊）。</li>
 *   <li>{@code /api/admin/static/**}：管理員頭像等圖片的靜態資源，也直接放行——瀏覽器
 *       原生 {@code <img>} 標籤不會帶 Authorization header，這條路徑若強制驗證圖片就顯示不出來。
 *       只開這一條純讀取的靜態資源路徑，查/改個人資料等 JSON API 不受影響，一樣強制登入
 *       （tengan-admin 自己的 SecurityConfig 也有對應的放行鏈，兩邊要一致）。</li>
 *   <li>{@code /api/customer/cart/**}：選擇性驗證——驗證成功才轉發 token，
 *       失敗或缺失一律放行但不轉發 Authorization，下游依 header 存不存在判斷會員/訪客</li>
 *   <li>其餘 {@code /api/customer/**}：強制登入，缺失或驗證失敗直接 401，用
 *       {@code userJwtDecoder}（tengan-auth 簽的 customer token）驗證。</li>
 *   <li>其餘 {@code /api/admin/**}：強制登入，缺失或驗證失敗直接 401，用
 *       {@code adminJwtDecoder}（tengan-admin 自己簽的 admin token）驗證——admin_user 帳密
 *       不在 tengan-auth 的 account 表，是獨立主體，不能沿用 customer 的簽章金鑰。</li>
 * </ul>
 */
@Component
public class UserJwtFilter implements GlobalFilter, Ordered {

    private static final String PUBLIC_PREFIX = "/api/public/";
    private static final String PARTNER_PREFIX = "/api/partner/";
    private static final String ADMIN_PREFIX = "/api/admin/";
    private static final String ADMIN_STATIC_PREFIX = "/api/admin/static/";
    private static final String CART_SELECTIVE_PREFIX = "/api/customer/cart/";
    private static final String CUSTOMER_REFRESH_PATH = "/api/customer/auth/refresh";
    private static final String ADMIN_LOGIN_PATH = "/api/admin/auth/login";
    private static final String ADMIN_REFRESH_PATH = "/api/admin/auth/refresh";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtDecoder userJwtDecoder;
    private final JwtDecoder adminJwtDecoder;

    public UserJwtFilter(JwtDecoder userJwtDecoder, JwtDecoder adminJwtDecoder) {
        this.userJwtDecoder = userJwtDecoder;
        this.adminJwtDecoder = adminJwtDecoder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith(PUBLIC_PREFIX) || path.startsWith(PARTNER_PREFIX) || path.equals(CUSTOMER_REFRESH_PATH)
                || path.equals(ADMIN_LOGIN_PATH) || path.equals(ADMIN_REFRESH_PATH)
                || path.startsWith(ADMIN_STATIC_PREFIX)) {
            return chain.filter(exchange);
        }

        boolean selectiveAuth = path.startsWith(CART_SELECTIVE_PREFIX);
        boolean adminTier = path.startsWith(ADMIN_PREFIX);
        JwtDecoder decoder = adminTier ? adminJwtDecoder : userJwtDecoder;
        String token = extractBearerToken(exchange.getRequest());

        if (token == null) {
            return selectiveAuth ? chain.filter(stripAuthorization(exchange)) : unauthorized(exchange);
        }

        // NimbusJwtDecoder#decode 是阻塞呼叫（JWK 已快取在本地，純 CPU 驗簽，但仍需離開
        // event loop 執行緒，避免阻塞 Reactor Netty 的少量 worker 執行緒）。
        return Mono.fromCallable(() -> decoder.decode(token))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(jwt -> chain.filter(exchange))
                .onErrorResume(JwtException.class,
                        e -> selectiveAuth ? chain.filter(stripAuthorization(exchange)) : unauthorized(exchange));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private String extractBearerToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private ServerWebExchange stripAuthorization(ServerWebExchange exchange) {
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION))
                .build();
        return exchange.mutate().request(mutatedRequest).build();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
