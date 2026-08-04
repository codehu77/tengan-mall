package com.tengan.mall.auth.application.port;

import java.util.Optional;

/**
 * Refresh token 是 opaque random string（非 JWT），Server 端存 Redis
 * {@code auth:refresh:{tokenId}} → {@code {accountId, familyId, used}}，TTL 7~14 天
 * （微服務前台API待開發清單.md 第2節「Access Token + Refresh Token（無黑名單）」）。
 * tokenId 本身就是回給前端的 refresh token 值，不是另外映射的內部 id。
 */
public interface RefreshTokenStorePort {

    /** 全新登入：產生新的 tokenId + familyId。 */
    String issue(Long accountId);

    Optional<RefreshTokenEntry> find(String tokenId);

    /** Rotation：標記舊 token 已使用，同一個 familyId 下發新 token。 */
    String rotate(String oldTokenId, RefreshTokenEntry entry);

    /** Reuse detection 觸發：撤銷整個 family，強制該 session 所有裝置重新登入。 */
    void revokeFamily(String familyId);

    /** 登出：刪除單一 token（不影響同帳號其他裝置的 session）。 */
    void delete(String tokenId);
}
