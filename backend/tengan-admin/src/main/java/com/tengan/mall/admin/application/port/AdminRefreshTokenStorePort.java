package com.tengan.mall.admin.application.port;

import java.util.Optional;

/**
 * Refresh token 是 opaque random string，存 Redis {@code admin:refresh:{tokenId}}，
 * rotation + reuse detection 邏輯比照 tengan-auth 的 RefreshTokenStorePort。
 */
public interface AdminRefreshTokenStorePort {

    String issue(Long adminUserId);

    Optional<AdminRefreshTokenEntry> find(String tokenId);

    String rotate(String oldTokenId, AdminRefreshTokenEntry entry);

    void revokeFamily(String familyId);

    void delete(String tokenId);
}
