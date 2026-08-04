package com.tengan.mall.jwt;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

/**
 * 驗證 X-Identity-Assertion header（服務間呼叫時，轉發的操作者原始 JWT）。這代表「操作者身份」跟
 * 「一般登入」是同一顆信任的 token，只是換了個 header 名稱轉發，不是另一套簽章機制（見
 * docs/JWT設計.md 服務間呼叫要不要轉發 X-Identity-Assertion 的判斷準則）。
 *
 * <p>用哪一顆 JwtDecoder 驗簽，取決於轉發方是哪種主體——customer 前台呼叫用 userJwtDecoder，
 * tengan-admin 後台 BFF 呼叫用 adminJwtDecoder，兩種主體各自一把金鑰，不能共用同一個解碼器
 * （見 {@link JwtVerificationAutoConfiguration} 產生的 userIdentityAssertionVerifier /
 * adminIdentityAssertionVerifier 兩顆獨立 bean）。</p>
 */
public class IdentityAssertionVerifier {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtDecoder decoder;

    public IdentityAssertionVerifier(JwtDecoder decoder) {
        this.decoder = decoder;
    }

    /**
     * @param headerValue 原始 header 值，例如 "Bearer eyJhbGciOi..."
     * @return 解出的操作者 ID（JWT 的 sub claim）
     * @throws JwtException 簽章無效、過期或格式不對時拋出，呼叫端應該回應 401/403，不能吞掉繼續執行
     */
    public String verifyAndExtractSubject(String headerValue) {
        return verify(headerValue).getSubject();
    }

    /**
     * 回傳完整解碼後的 Jwt，讓呼叫端可以讀取除了 sub 以外的自訂 claim（例如 admin JWT 的
     * "username" claim——tengan-admin 的 sub 是數字 adminUserId，下游服務通常想要的是人類可讀
     * 的使用者名稱，不是重新查一次 tengan-admin 的 DB）。
     *
     * @param headerValue 原始 header 值，例如 "Bearer eyJhbGciOi..."
     * @throws JwtException 簽章無效、過期或格式不對時拋出，呼叫端應該回應 401/403，不能吞掉繼續執行
     */
    public Jwt verify(String headerValue) {
        if (headerValue == null || !headerValue.startsWith(BEARER_PREFIX)) {
            throw new JwtException("X-Identity-Assertion 格式不正確，缺少 Bearer 前綴");
        }
        String token = headerValue.substring(BEARER_PREFIX.length());
        return decoder.decode(token);
    }
}
