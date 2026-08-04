package com.tengan.mall.admin.domain.model;

/**
 * colon 式權限碼（例如 system:user:write），對應 JWT 的 permissions claim 跟
 * {@code @PreAuthorize("hasAuthority(...)")}。跟 OAuth2 scope 的 dot 式命名是兩套系統，
 * 不要混用（見 docs/JWT設計.md 第四節）。
 */
public record PermissionCode(String value) {

    public PermissionCode {
        if (value == null || !value.matches("^[a-z][a-z0-9]*(:[a-z][a-z0-9]*)+$")) {
            throw new IllegalArgumentException("permissionCode 格式錯誤，需為 colon 分隔的小寫片段: " + value);
        }
    }
}
