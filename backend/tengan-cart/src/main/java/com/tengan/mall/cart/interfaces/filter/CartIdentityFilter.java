package com.tengan.mall.cart.interfaces.filter;

import com.tengan.mall.cart.application.cart.CartOwner;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 對 /api/customer/cart/** 解析身份：Authorization Bearer token 若存在且驗簽成功，視為會員
 * （zero-trust，自己重新驗簽，不依賴 Gateway 的選擇性驗證結果）；否則視為訪客，讀取/發放
 * tengan_guest_key cookie（見 cart_storage_decision）。這個 SecurityConfig 對 /api/customer/cart/**
 * 是 permitAll——身份判斷刻意不用 Spring Security 的驗證鏈，因為同一個 URL 前綴下的請求本來就要
 * 同時接受「有登入」跟「沒登入」兩種情況，不是強制驗證。
 *
 * <p>Cookie 只在「訪客」情境下才會新發——已登入的使用者沒有必要另外起一個訪客購物車 session；
 * 但若 cookie 早已存在（例如登入前先加過商品），即使已登入這裡仍會把既有 guestKey 一併解析出來，
 * 供 merge 端點使用。</p>
 */
@Component
public class CartIdentityFilter extends OncePerRequestFilter {

    private static final String CART_PATH_PREFIX = "/api/customer/cart/";
    private static final String GUEST_COOKIE_NAME = "tengan_guest_key";
    private static final Duration GUEST_COOKIE_MAX_AGE = Duration.ofDays(30);
    private static final String BEARER_PREFIX = "Bearer ";

    public static final String MEMBER_ID_ATTRIBUTE = "cart.memberId";
    public static final String GUEST_KEY_ATTRIBUTE = "cart.guestKey";

    private final JwtDecoder userJwtDecoder;

    public CartIdentityFilter(JwtDecoder userJwtDecoder) {
        this.userJwtDecoder = userJwtDecoder;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith(CART_PATH_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Long memberId = resolveMemberId(request);
        String existingGuestKey = readGuestCookie(request);

        if (memberId != null) {
            request.setAttribute(MEMBER_ID_ATTRIBUTE, memberId);
            if (existingGuestKey != null) {
                request.setAttribute(GUEST_KEY_ATTRIBUTE, existingGuestKey);
            }
        } else {
            String guestKey = existingGuestKey != null ? existingGuestKey : issueGuestCookie(response);
            request.setAttribute(GUEST_KEY_ATTRIBUTE, guestKey);
        }

        chain.doFilter(request, response);
    }

    private Long resolveMemberId(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        try {
            var jwt = userJwtDecoder.decode(header.substring(BEARER_PREFIX.length()));
            return Long.valueOf(jwt.getSubject());
        } catch (JwtException e) {
            return null;
        }
    }

    private String readGuestCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (GUEST_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String issueGuestCookie(HttpServletResponse response) {
        String guestKey = UUID.randomUUID().toString();
        ResponseCookie cookie = ResponseCookie.from(GUEST_COOKIE_NAME, guestKey)
                .httpOnly(true)
                .path("/")
                .maxAge(GUEST_COOKIE_MAX_AGE)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return guestKey;
    }

    /** 一般端點（非 merge）用：已登入一律當 Member，否則當 Guest。 */
    public static CartOwner resolveOwner(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute(MEMBER_ID_ATTRIBUTE);
        if (memberId != null) {
            return new CartOwner.Member(memberId);
        }
        return new CartOwner.Guest((String) request.getAttribute(GUEST_KEY_ATTRIBUTE));
    }

    public static Long resolveMemberIdOrNull(HttpServletRequest request) {
        return (Long) request.getAttribute(MEMBER_ID_ATTRIBUTE);
    }

    public static Optional<String> resolveGuestKey(HttpServletRequest request) {
        return Optional.ofNullable((String) request.getAttribute(GUEST_KEY_ATTRIBUTE));
    }
}
