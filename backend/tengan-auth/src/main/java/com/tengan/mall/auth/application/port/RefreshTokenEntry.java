package com.tengan.mall.auth.application.port;

/** 對應 Redis 裡 auth:refresh:{tokenId} 的內容（微服務前台API待開發清單.md 第2節）。 */
public record RefreshTokenEntry(Long accountId, String familyId, boolean used) {
}
