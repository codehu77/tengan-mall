package com.tengan.mall.admin.application.port;

import com.tengan.mall.admin.domain.model.AdminUserId;
import java.util.List;

/**
 * 簽發管理員 access token（JWT，claims 含 adminId/username/permissions，TTL 15 分鐘）。
 * 用 tengan-admin 自己的 RSA keypair 簽，透過自己的 /oauth2/jwks 公開驗簽金鑰——admin_user
 * 帳密不在 tengan-auth 的 account 表，是獨立主體，不能沿用 tengan-auth 的簽章金鑰。
 */
public interface AdminAccessTokenIssuerPort {

    String issue(AdminUserId adminUserId, String username, List<String> permissions);
}
