package com.tengan.mall.auth.application.port;

/**
 * 各家 provider（Google/Facebook/...）驗證通過後正規化成的統一形狀，上層 application 邏輯
 * 不需要知道底下是哪家 provider 的原始 claim 長什麼樣子。
 */
public record OAuthUserInfo(String providerUserId, String email, boolean emailVerified, String displayName,
        String avatarUrl) {
}
