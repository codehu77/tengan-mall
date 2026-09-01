package com.tengan.mall.auth.application.port;

/**
 * 對應 Redis 裡 auth:refresh:{tokenId} 的內容（微服務前台API待開發清單.md 第2節）。
 * rememberMe 記錄這個 session 當初是不是 remember-me 登入的，rotation 時要沿用同一個 TTL 類別，
 * 不能因為換了一顆新 token 就把 30 天的 session 打回 1 天。
 */
public record RefreshTokenEntry(Long accountId, String familyId, boolean used, boolean rememberMe) {
}
