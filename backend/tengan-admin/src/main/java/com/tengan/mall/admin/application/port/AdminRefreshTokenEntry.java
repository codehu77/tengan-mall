package com.tengan.mall.admin.application.port;

/** 對應 Redis 裡 admin:refresh:{tokenId} 的內容，結構比照 tengan-auth 的 RefreshTokenEntry。 */
public record AdminRefreshTokenEntry(Long adminUserId, String familyId, boolean used) {
}
