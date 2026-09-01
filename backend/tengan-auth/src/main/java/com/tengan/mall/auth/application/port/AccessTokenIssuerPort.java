package com.tengan.mall.auth.application.port;

import com.tengan.mall.auth.domain.model.AccountId;

/**
 * 簽發使用者 access token（JWT，claims 只有 userId，TTL 15 分鐘，zero-trust：不放明文身份
 * claim，下游只驗簽章+exp+sub）。實作見 infrastructure/security，用 Nimbus 簽出的 token 透過
 * /oauth2/jwks 公開驗簽金鑰，供 Gateway 與其他服務的 userJwtDecoder 驗證（docs/JWT設計.md 第一節）。
 */
public interface AccessTokenIssuerPort {

    String issue(AccountId accountId);
}
